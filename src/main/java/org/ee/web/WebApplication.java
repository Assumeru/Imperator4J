package org.ee.web;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.ee.web.request.AbstractRequestResolver;

public abstract class WebApplication extends Application {
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(getRequestResolver());
		return classes;
	}

	protected abstract Class<? extends AbstractRequestResolver> getRequestResolver();
}
