package com.ee.imperator.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.json.JSONObject;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.api.handlers.Param;
import com.ee.imperator.api.handlers.Endpoint;
import com.ee.imperator.api.handlers.Endpoint.Mode;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.user.Member;

/**
 * Provides a standard way of interacting with games through simple requests.
 */
public class Api {
	private static final Logger LOG = LogManager.createLogger();
	public static final String DATE_ATOM = "yyyy-MM-dd'T'HH:mm:ssXXX";
	private final LongPolling longPolling;
	private final WebSocket webSocket;
	private final InternalApi internal;
	private final Map<String, List<Handler>> handlers;
	private final List<RequestListener> listeners;
	private final ImperatorApplicationContext context;

	/**
	 * Creates a new API.
	 * 
	 * @param context The context to use
	 */
	public Api(ImperatorApplicationContext context) {
		this.context = context;
		handlers = getHandlers();
		listeners = new ArrayList<>();
		longPolling = new LongPolling(this);
		webSocket = new WebSocket(this);
		internal = new InternalApi(this);
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<Handler>> getHandlers() {
		Map<String, List<Handler>> found = new HashMap<>();
		Set<Class<?>> types = new Reflections(Endpoint.class.getPackage().getName()).getTypesAnnotatedWith(Endpoint.class);
		for(Class<?> type : types) {
			if(type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
				continue;
			}
			findHandler(found, ReflectionUtils.getAllMethods(type, method -> "handle".equals(method.getName())), type);
		}
		for(List<Handler> list : found.values()) {
			list.sort(null);
			((ArrayList<?>) list).trimToSize();
		}
		return Collections.unmodifiableMap(found);
	}

	JSONObject handleRequest(Map<String, ?> variables, Member member) throws RequestException {
		try {
			JSONObject output = handleInternal(variables, Objects.requireNonNull(member));
			runListeners(member, variables, output);
			return output;
		} catch(RequestException e) {
			throw e;
		} catch(Exception e) {
			LOG.e("Failed to handle request", e);
			throw new RequestException("Fatal error", Endpoint.Mode.of(variables.get("mode")), String.valueOf(variables.get("type")), e);
		}
	}

	private JSONObject handleInternal(Map<String, ?> variables, Member member) throws RequestException, IllegalAccessException {
		Endpoint.Mode mode = Endpoint.Mode.of(variables.get("mode"));
		String type = String.valueOf(variables.get("type"));
		try {
			for(Handler handler : getHandlers(mode, type)) {
				Match match = handler.getMatch(variables, member);
				if(match != null) {
					JSONObject out = handler.invoke(match);
					return getReply(out, mode, type);
				}
			}
		} catch(InvocationTargetException e) {
			if(e.getCause() instanceof RequestException) {
				throw (RequestException) e.getCause();
			}
			LOG.e("Failed to handle request", e);
			throw new RequestException("Fatal error", mode, type, e);
		}
		throw new InvalidRequestException("Unknown request", mode, type);
	}

	private List<Handler> getHandlers(Endpoint.Mode mode, String type) {
		List<Handler> list = handlers.get(getKey(mode, type));
		if(list != null) {
			return list;
		}
		return Collections.emptyList();
	}

	private void findHandler(Map<String, List<Handler>> handlers, Set<Method> methods, Class<?> type) {
		Endpoint request = type.getAnnotation(Endpoint.class);
		if(request.type() == null || request.type().isEmpty() || request.mode() == null) {
			LOG.w("Classes annotated with " + Endpoint.class + " must specify a non-empty mode and type");
			return;
		}
		Object instance;
		try {
			instance = getInstance(type);
		} catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
			LOG.w("Classes annotated with " + Endpoint.class + " must specify a default constructor", e);
			return;
		}
		addHandlers(handlers, methods, type, getKey(request.mode(), request.type()), instance);
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

	private void addHandlers(Map<String, List<Handler>> handlers, Set<Method> methods, Class<?> type, String key, Object instance) {
		for(Method method : methods) {
			if((method.getReturnType() == void.class || method.getReturnType().isAssignableFrom(JSONObject.class)) && method.getParameterCount() > 0) {
				Parameter[] params = method.getParameters();
				if(!params[0].getType().isAssignableFrom(Member.class) || !parameterised(params)) {
					continue;
				}
				addHandler(handlers, key, new Handler(instance, method));
				LOG.d("Loaded API handler " + method + " on " + type);
			}
		}
	}

	private String getKey(Endpoint.Mode mode, String type) {
		return mode + "_" + type;
	}

	private void addHandler(Map<String, List<Handler>> handlers, String key, Handler handler) {
		List<Handler> list = handlers.get(key);
		if(list == null) {
			list = new ArrayList<>();
			handlers.put(key, list);
		}
		list.add(handler);
	}

	private boolean parameterised(Parameter[] params) {
		for(int i = 1; i < params.length; i++) {
			Param annotation = params[i].getAnnotation(Param.class);
			if(annotation == null) {
				return false;
			} else if(annotation.value() == null || annotation.value().isEmpty()) {
				LOG.w("Params annotated with " + Param.class + " should specify a non-empty name");
				return false;
			}
		}
		return true;
	}

	/**
	 * @param error The error message
	 * @param mode The request mode
	 * @param type The request type
	 * @return A standard reply containing the given error message
	 */
	public static String getErrorMessage(String error, Mode mode, String type) {
		return getReply(new JSONObject().put("error", error), mode, type).toString();
	}

	private static JSONObject getReply(JSONObject reply, Mode mode, String type) {
		if(reply == null) {
			return null;
		}
		return reply.put("request", new JSONObject().put("mode", mode.toString()).put("type", type));
	}

	/**
	 * Registers a listener to be called on successful API calls.
	 * 
	 * @param listener The listener to register
	 */
	public void addRequestListener(RequestListener listener) {
		listeners.add(listener);
	}

	/**
	 * Deregisters a listener.
	 * 
	 * @param listener The listener to remove
	 */
	public void removeRequestListener(RequestListener listener) {
		listeners.remove(listener);
	}

	private void runListeners(Member member, Map<String, ?> input, JSONObject output) {
		for(RequestListener listener : listeners) {
			try {
				listener.onRequest(member, input, output);
			} catch(Exception e) {
				LOG.e(e);
			}
		}
	}

	ImperatorApplicationContext getContext() {
		return context;
	}

	public LongPolling getLongPolling() {
		return longPolling;
	}

	public WebSocket getWebSocket() {
		return webSocket;
	}

	public InternalApi getInternal() {
		return internal;
	}
}
