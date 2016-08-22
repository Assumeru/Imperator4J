package com.ee.imperator;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import org.ee.config.Config;
import org.ee.config.ConfigurationException;
import org.ee.logger.LogManager;
import org.ee.logger.LogProvider;
import org.ee.logger.Logger;
import org.ee.reflection.ReflectionUtils;
import org.ee.web.WebApplication;
import org.ee.web.request.RequestHandler;
import org.ee.web.response.ResponseWriter;

import com.ee.imperator.api.Api;
import com.ee.imperator.config.ImperatorClassLoader;
import com.ee.imperator.config.ImperatorConfig;
import com.ee.imperator.crypt.PasswordHasher;
import com.ee.imperator.crypt.bcrypt.BCryptHasher;
import com.ee.imperator.crypt.csrf.CSRFTokenBuilder;
import com.ee.imperator.data.ChatState;
import com.ee.imperator.data.GameState;
import com.ee.imperator.data.JoinedState;
import com.ee.imperator.data.MemberState;
import com.ee.imperator.data.State;
import com.ee.imperator.i18n.ClientSideLanguageProvider;
import com.ee.imperator.map.MapProvider;
import com.ee.imperator.task.CleanUp;
import com.ee.imperator.template.TemplateProvider;
import com.ee.imperator.url.UrlBuilder;
import com.ee.imperator.websocket.WebSocket;
import com.ee.imperator.websocket.WebSocketConfigurator;

@WebServlet(urlPatterns = "/*")
public class Imperator extends WebApplication {
	private static final long serialVersionUID = -6119169366807789039L;
	private static final Logger LOG = LogManager.createLogger();
	private final ImperatorClassLoader classLoader;
	private ImperatorApplicationContext context;
	private Config config;
	private State state;
	private PasswordHasher hasher;
	private UrlBuilder urlBuilder;
	private ClientSideLanguageProvider languageProvider;
	private TemplateProvider templateProvider;
	private MapProvider mapProvider;
	private RequestHandler requestHandler;
	private ResponseWriter responseWriter;
	private CSRFTokenBuilder csrfTokenBuilder;
	private CleanUp cleanup;
	private Api api;

	public Imperator() {
		classLoader = new ImperatorClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
	}

	@Override
	public void init() throws ServletException {
		context = new ImperatorContext(this);
		config = initConfig();
		LogManager.setLogProvider(getProviderInstance(LogProvider.class));
		api = new Api(context);
		state = initState();
		hasher = initHasher();
		urlBuilder = getProviderInstance(UrlBuilder.class);
		languageProvider = getProviderInstance(ClientSideLanguageProvider.class);
		templateProvider = getProviderInstance(TemplateProvider.class);
		mapProvider = getProviderInstance(MapProvider.class);
		requestHandler = getProviderInstance(RequestHandler.class);
		responseWriter = getProviderInstance(ResponseWriter.class);
		csrfTokenBuilder = getProviderInstance(CSRFTokenBuilder.class);
		cleanup = new CleanUp(context);
		initWebSocket();
	}

	@SuppressWarnings("unchecked")
	private <T> T getInstance(Class<T> type) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		Class<? extends ImperatorApplicationContext> contextType = context.getClass();
		for(Constructor<?> constructor : type.getConstructors()) {
			if(constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].isAssignableFrom(contextType)) {
				return (T) constructor.newInstance(context);
			}
		}
		return type.newInstance();
	}

	private <T> T getProviderInstance(Class<T> type) {
		try {
			return getInstance(ReflectionUtils.getSubclass(config.getClass(type, null), type));
		} catch(Exception e) {
			throw new ConfigurationException("Failed to create provider for " + type, e);
		}
	}

	private Config initConfig() {
		try {
			String config = System.getProperty("com.ee.imperator.Config");
			if(config == null) {
				config = ImperatorConfig.class.getName();
				LOG.w("Using default config, use jvm argument -Dcom.ee.imperator.Config=<class name> to define another config implementation.");
			}
			return getInstance(ReflectionUtils.getSubclass(config, classLoader, Config.class));
		} catch(Exception e) {
			throw new ConfigurationException("Failed to init config", e);
		}
	}

	private State initState() {
		try {
			return new JoinedState(getProviderInstance(GameState.class),
					getProviderInstance(MemberState.class),
					getProviderInstance(ChatState.class));
		} catch(Exception e) {
			throw new ConfigurationException("Failed to init state", e);
		}
	}

	private PasswordHasher initHasher() {
		try {
			return getInstance(ReflectionUtils.getSubclass(getConfig().getClass(PasswordHasher.class, null, BCryptHasher.class), PasswordHasher.class));
		} catch(Exception e) {
			throw new ConfigurationException("Failed to init password hasher", e);
		}
	}

	private void initWebSocket() {
		ServerContainer serverContainer = (ServerContainer) getServletContext().getAttribute(ServerContainer.class.getName());
		if(serverContainer == null) {
			LOG.e("Failed to init websockets");
			return;
		}
		ServerEndpointConfig endpoint = ServerEndpointConfig.Builder.create(WebSocket.class, com.ee.imperator.api.WebSocket.PATH).configurator(new WebSocketConfigurator(context)).build();
		try {
			serverContainer.addEndpoint(endpoint);
		} catch(DeploymentException e) {
			throw new ConfigurationException("Failed to init websockets", e);
		}
	}

	@Override
	protected RequestHandler getRequestHandler() {
		return requestHandler;
	}

	@Override
	protected ResponseWriter getResponseWriter() {
		return responseWriter;
	}

	public Config getConfig() {
		return config;
	}

	public State getState() {
		return state;
	}

	public ClientSideLanguageProvider getLanguageProvider() {
		return languageProvider;
	}

	public MapProvider getMapProvider() {
		return mapProvider;
	}

	public PasswordHasher getHasher() {
		return hasher;
	}

	public UrlBuilder getUrlBuilder() {
		return urlBuilder;
	}

	public TemplateProvider getTemplateProvider() {
		return templateProvider;
	}

	public Api getApi() {
		return api;
	}

	public CSRFTokenBuilder getCsrfTokenBuilder() {
		return csrfTokenBuilder;
	}

	@Override
	public void destroy() {
		if(cleanup != null) {
			cleanup.stop();
		}
		if(state != null) {
			try {
				state.close();
			} catch (IOException e) {
				LOG.e("Failed to close dataProvider", e);
			}
		}
	}
}
