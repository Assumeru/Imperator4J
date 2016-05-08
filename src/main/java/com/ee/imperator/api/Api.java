package com.ee.imperator.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.json.JSONObject;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import com.ee.imperator.api.handlers.Param;
import com.ee.imperator.api.handlers.Request;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.user.Member;

public class Api {
	private static final Logger LOG = LogManager.createLogger();
	public static final LongPolling LONG_POLLING = new LongPolling();
	public static final String DATE_ATOM = "yyyy-MM-dd'T'HH:mm:ssXXX";
	private static Map<String, List<Handler>> handlers;

	static String handleRequest(Map<String, String> variables, Member member) throws RequestException {
		try {
			JSONObject output = handleInternal(variables, member);
			return output == null ? null : output.toString();
		} catch(InvocationTargetException e) {
			if(e.getCause() instanceof RequestException) {
				throw (RequestException) e.getCause();
			}
			LOG.e("Failed to handle request", e);
			throw new RequestException("Fatal error", variables.get("mode"), variables.get("type"), e);
		} catch(RequestException e) {
			throw e;
		} catch(Exception e) {
			LOG.e("Failed to handle request", e);
			throw new RequestException("Fatal error", variables.get("mode"), variables.get("type"), e);
		}
	}

	private static JSONObject handleInternal(Map<String, String> variables, Member member) throws Exception {
		String mode = variables.get("mode");
		String type = variables.get("type");
		for(Handler handler : getHandlers(mode, type)) {
			Match match = handler.getMatch(variables, member);
			if(match != null) {
				JSONObject out = handler.invoke(match);
				return getReply(out, mode, type);
			}
		}
		throw new InvalidRequestException("Unknown request", mode, type);
	}

	private static JSONObject getReply(JSONObject reply, String mode, String type) {
		if(reply == null) {
			return null;
		}
		return reply.put("request", new JSONObject().put("mode", mode).put("type", type));
	}

	private static List<Handler> getHandlers(String mode, String type) {
		if(handlers == null) {
			setHandlers();
		}
		List<Handler> list = handlers.get(getKey(mode, type));
		if(list != null) {
			return list;
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	private synchronized static void setHandlers() {
		if(handlers == null) {
			handlers = new HashMap<>();
			Set<Class<?>> types = new Reflections(Request.class.getPackage().getName()).getTypesAnnotatedWith(Request.class);
			for(Class<?> type : types) {
				if(type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
					continue;
				}
				findHandler(ReflectionUtils.getAllMethods(type, method -> "handle".equals(method.getName())), type);
			}
			for(List<Handler> list : handlers.values()) {
				list.sort(null);
				((ArrayList<?>) list).trimToSize();
			}
		}
	}

	private static void findHandler(Set<Method> methods, Class<?> type) {
		Request request = type.getAnnotation(Request.class);
		if(request.type() == null || request.type().isEmpty() || request.mode() == null || request.mode().isEmpty()) {
			LOG.w("Classes annotated with " + Request.class + " must specify a non-empty mode and type");
			return;
		}
		String key = getKey(request.mode(), request.type());
		Object instance;
		try {
			instance = type.newInstance();
		} catch(InstantiationException | IllegalAccessException e) {
			LOG.w("Classes annotated with " + Request.class + " must specify a default constructor", e);
			return;
		}
		for(Method method : methods) {
			if((method.getReturnType() == void.class || method.getReturnType().isAssignableFrom(JSONObject.class)) && method.getParameterCount() > 0) {
				Parameter[] params = method.getParameters();
				if(!params[0].getType().isAssignableFrom(Member.class) || !parameterised(params)) {
					continue;
				}
				addHandler(key, new Handler(instance, method));
				LOG.d("Loaded API handler " + method + " on " + type);
			}
		}
	}

	private static String getKey(String mode, String type) {
		return mode + "_" + type;
	}

	private static void addHandler(String key, Handler handler) {
		List<Handler> list = handlers.get(key);
		if(list == null) {
			list = new ArrayList<>();
			handlers.put(key, list);
		}
		list.add(handler);
	}

	private static boolean parameterised(Parameter[] params) {
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

	public static String getErrorMessage(String error, String mode, String type) {
		return getReply(new JSONObject().put("error", error), mode, type).toString();
	}

	private Api() {
	}
}
