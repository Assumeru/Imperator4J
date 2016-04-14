package org.ee.web.request;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.request.page.Ignore;
import org.ee.web.request.page.NavigationPage;
import org.ee.web.request.page.WebPage;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

public abstract class AbstractRequestResolver {
	private static final Logger LOG = LogManager.createLogger();
	private static List<WebPage> navigation;
	private static Set<RequestHandler> requestHandlers;
	@Context
	private HttpServletRequest request;
	@Context
	private HttpServletResponse response;
	@Context
	private ServletContext servletContext;

	@Path("/{path: (.*)}")
	@GET
	@POST
	public Response handleRequest() {
		String path = getPath();
		RequestHandler page = getHandler(path);
		return handleRequest(page, path);
	}

	protected HttpServletRequest getRequest() {
		return request;
	}

	protected HttpServletResponse getResponse() {
		return response;
	}

	protected ServletContext getServletContext() {
		return servletContext;
	}

	protected List<WebPage> getNavigation() {
		return navigation;
	}

	private Response handleRequest(RequestHandler handler, String path) {
		Request request = new Request(servletContext, this.request, path);
		Object context = createContext(request);
		request.setContext(context);
		try {
			return handler.getResponse(request);
		} catch (Exception e) {
			LOG.e("Error handling request using " + handler, e);
		}
		return getErrorHandler().getResponse(request);
	}

	protected abstract RequestHandler getErrorHandler();

	protected abstract RequestHandler getDefaultHandler();

	protected abstract Object createContext(Request request);

	private String getPath() {
		try {
			return request.getRequestURI().substring(request.getContextPath().length() + 1);
		} catch(StringIndexOutOfBoundsException e) {
			return "";
		}
	}

	private RequestHandler getHandler(String path) {
		if(requestHandlers == null) {
			initRequestHandlers();
		}
		if(path.contains("../")) {
			//Is this even possible?
			LOG.e("Path contains ../");
			return getErrorHandler();
		}
		for(RequestHandler handler : requestHandlers) {
			if(handler.matches(path)) {
				return handler;
			}
		}
		LOG.i("No handler found for " + path);
		return getDefaultHandler();
	}

	private synchronized void initRequestHandlers() {
		if(requestHandlers == null) {
			requestHandlers = new HashSet<>();
			navigation = new ArrayList<>();
			Set<Class<? extends RequestHandler>> handlers = getRequestHandlers();
			for(Class<? extends RequestHandler> handler : handlers) {
				if(!Modifier.isAbstract(handler.getModifiers()) && !handler.isAnnotationPresent(Ignore.class)) {
					try {
						RequestHandler instance = handler.newInstance();
						requestHandlers.add(instance);
						if(instance instanceof WebPage && handler.isAnnotationPresent(NavigationPage.class)) {
							navigation.add((WebPage) instance);
						}
						LOG.d("Loaded handler " + instance.getClass());
					} catch (ClassCastException e) {
						LOG.e("Classes annotated with NavigationPage should implement WebPage", e);
					} catch (InstantiationException | IllegalAccessException e) {
						LOG.e("Implementations of RequestHandler should provide a default constructor", e);
					}
				}
			}
			navigation.sort(new NavigationPage.NavigationSorter());
			((ArrayList<?>) navigation).trimToSize();
			LOG.d("Loaded " + navigation.size() + " navigation pages");
		}
	}

	protected Set<Class<? extends RequestHandler>> getRequestHandlers() {
		return new Reflections(new ConfigurationBuilder().forPackages(getClass().getPackage().getName()).forPackages(getPackages())).getSubTypesOf(RequestHandler.class);
	}

	protected abstract String[] getPackages();
}
