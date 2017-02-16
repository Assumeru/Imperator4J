package com.ee.imperator.web;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ee.collection.MapBuilder;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.Status;
import org.ee.web.request.Request;
import org.ee.web.request.RequestHandler;
import org.ee.web.request.filter.RequestFilter;
import org.ee.web.request.filter.RequestFilterHandler;
import org.ee.web.request.resource.ResourceHandler;
import org.ee.web.response.Response;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.web.page.Http403;
import com.ee.imperator.web.page.Http404;
import com.ee.imperator.web.page.Http500;

public class ImperatorRequestHandler extends RequestFilterHandler {
	private static final Logger LOG = LogManager.createLogger();
	private final Map<Status, WebPage> statusPages;
	private final ImperatorApplicationContext context;
	private final Set<RequestFilter> filters;
	private final List<RequestFilter> navigationPages;

	public ImperatorRequestHandler(ImperatorApplicationContext context) {
		this.context = context;
		this.filters = initRequestHandlers();
		this.navigationPages = initNavigationPages();
		statusPages = new MapBuilder<Status, WebPage>()
				.put(Status.FORBIDDEN, new Http403(this))
				.put(Status.NOT_FOUND, new Http404(this))
				.put(Status.INTERNAL_SERVER_ERROR, new Http500(this))
				.build(true);
	}

	@Override
	public Response handle(Request request) {
		Response response = super.handle(request);
		if(context.getCsrfTokenBuilder().shouldSetToken(request)) {
			context.getCsrfTokenBuilder().setToken(request, response);
		}
		return response;
	}

	private Set<RequestFilter> initRequestHandlers() {
		Set<RequestFilter> requestHandlers = new HashSet<>();
		for(Class<? extends RequestFilter> handler : getHandlers()) {
			if(!Modifier.isAbstract(handler.getModifiers()) && !handler.isAnnotationPresent(Ignore.class)) {
				try {
					RequestFilter instance = getInstance(handler);
					requestHandlers.add(instance);
					LOG.d("Loaded handler " + instance.getClass());
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					LOG.e("Implementations of RequestHandler should provide a default constructor", e);
				}
			}
		}
		return requestHandlers;
	}

	private static Set<Class<? extends RequestFilter>> getHandlers() {
		return new Reflections(new ConfigurationBuilder()
				.forPackages(ImperatorRequestHandler.class.getPackage().getName(), ResourceHandler.class.getPackage().getName()))
				.getSubTypesOf(RequestFilter.class);
	}

	private RequestFilter getInstance(Class<? extends RequestFilter> type) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		Class<? extends ImperatorRequestHandler> paramType = getClass();
		for(Constructor<?> constructor : type.getConstructors()) {
			if(constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].isAssignableFrom(paramType)) {
				return (RequestFilter) constructor.newInstance(this);
			}
		}
		return type.newInstance();
	}

	private List<RequestFilter> initNavigationPages() {
		ArrayList<RequestFilter> navigation = new ArrayList<>();
		for(RequestFilter filter : getFilters()) {
			if(filter.getClass().isAnnotationPresent(NavigationPage.class)) {
				navigation.add(filter);
			}
		}
		LOG.d("Loaded " + navigation.size() + " navigation pages");
		navigation.trimToSize();
		navigation.sort(new NavigationPage.NavigationSorter());
		return navigation;
	}

	@Override
	protected RequestHandler getStatusPage(Status status) {
		return statusPages.get(status);
	}

	public ImperatorApplicationContext getContext() {
		return context;
	}

	public List<RequestFilter> getNavigationPages() {
		return navigationPages;
	}

	@Override
	protected Set<RequestFilter> getFilters() {
		return filters;
	}
}
