package com.ee.imperator.request.page;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response.Status;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.reflection.MethodUtils;
import org.ee.text.PrimitiveUtils;
import org.ee.text.UriTemplate;

public abstract class AbstractVariablePage extends ImperatorPage {
	private static final Logger LOG = LogManager.createLogger();
	private UriTemplate template;
	private Method method;
	private String[] parameterOrder;

	public AbstractVariablePage(String pattern, String path, String template, int status, String title) {
		super(path, template, status, title);
		setTemplate(pattern);
	}

	public AbstractVariablePage(String pattern, String path, String template, Status status, String title) {
		super(path, template, status, title);
		setTemplate(pattern);
	}

	public AbstractVariablePage(String pattern, String path, String template, String title) {
		super(path, template, title);
		setTemplate(pattern);
	}

	public AbstractVariablePage(String pattern, String path, String template) {
		super(path, template);
		setTemplate(pattern);
	}

	private void setTemplate(String pattern) {
		template = new UriTemplate(pattern + "{___slash : [/]*}");
		List<Method> methods = MethodUtils.getMethodsUntil(getClass(), AbstractVariablePage.class);
		for(Method method : methods) {
			if(method.getName().equals("setVariables") && method.getParameterCount() > 0 && method.getParameterTypes()[0] == PageContext.class) {
				setMethod(method);
				break;
			}
		}
	}

	private void setMethod(Method method) {
		List<String> parameterOrder = new ArrayList<>();
		Parameter[] params = method.getParameters();
		for(int i = 1; i < params.length; i++) {
			Parameter param = params[i];
			PathParam annotation = param.getAnnotation(PathParam.class);
			if(annotation == null) {
				LOG.w("Parameter " + i + " in setVariables in " + getClass() + " is not annotated " + PathParam.class);
				return;
			} else if(!template.getParams().contains(annotation.value())) {
				LOG.w("UriTemplate for " + getClass() + " does not contain a parameter " + annotation.value());
				return;
			} else if(!PrimitiveUtils.isPrimitive(param.getType()) && !String.class.isAssignableFrom(param.getType())) {
				LOG.w(param.getType() + " in " + getClass() + " is not a valid argument type for setVariables");
				return;
			}
			parameterOrder.add(annotation.value());
		}
		this.method = method;
		this.parameterOrder = parameterOrder.toArray(new String[parameterOrder.size()]);
	}

	@Override
	protected final void setVariables(PageContext context) {
		if(method != null) {
			Object[] args = new Object[method.getParameterCount()];
			args[0] = context;
			Map<String, String> parameters = template.match(context.getPath());
			for(int i = 1; i < args.length; i++) {
				String param = parameters.get(parameterOrder[i - 1]);
				Class<?> type = method.getParameters()[i].getType();
				if(PrimitiveUtils.isPrimitive(type)) {
					args[i] = PrimitiveUtils.parse(type, param);
				} else {
					args[i] = param;
				}
			}
			try {
				method.invoke(this, args);
			} catch (InvocationTargetException e) {
				if(e.getCause() instanceof RuntimeException) {
					throw (RuntimeException) e.getCause();
				}
				throw new RuntimeException(e);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public boolean matches(String path) {
		return template.matches(path);
	}
}
