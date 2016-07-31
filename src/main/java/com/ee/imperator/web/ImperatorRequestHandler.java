package com.ee.imperator.web;

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
import org.ee.web.request.RequestHandler;
import org.ee.web.request.filter.RequestFilter;
import org.ee.web.request.filter.RequestFilterHandler;
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
	private final List<RequestFilter> navigationPages;

	public ImperatorRequestHandler(ImperatorApplicationContext context) {
		super(initRequestHandlers());
		this.context = context;
		this.navigationPages = init();
		statusPages = new MapBuilder<Status, WebPage>()
				.put(Status.FORBIDDEN, new Http403())
				.put(Status.NOT_FOUND, new Http404())
				.put(Status.INTERNAL_SERVER_ERROR, new Http500())
				.build(true);
		statusPages.values().forEach(p -> p.setRequestHandler(this));
	}

	private static Set<RequestFilter> initRequestHandlers() {
		Set<RequestFilter> requestHandlers = new HashSet<>();
		for(Class<? extends RequestFilter> handler : getHandlers()) {
			if(!Modifier.isAbstract(handler.getModifiers()) && !handler.isAnnotationPresent(Ignore.class)) {
				try {
					RequestFilter instance = handler.newInstance();
					requestHandlers.add(instance);
					LOG.d("Loaded handler " + instance.getClass());
				} catch (InstantiationException | IllegalAccessException e) {
					LOG.e("Implementations of RequestHandler should provide a default constructor", e);
				}
			}
		}
		return requestHandlers;
	}

	private List<RequestFilter> init() {
		ArrayList<RequestFilter> navigation = new ArrayList<>();
		for(RequestFilter filter : getFilters()) {
			if(filter.getClass().isAnnotationPresent(NavigationPage.class)) {
				navigation.add(filter);
			}
			if(filter instanceof WebPage) {
				((WebPage) filter).setRequestHandler(this);
			}
		}
		LOG.d("Loaded " + navigation.size() + " navigation pages");
		navigation.trimToSize();
		navigation.sort(new NavigationPage.NavigationSorter());
		return navigation;
	}

	private static Set<Class<? extends RequestFilter>> getHandlers() {
		return new Reflections(new ConfigurationBuilder().forPackages(ImperatorRequestHandler.class.getPackage().getName())).getSubTypesOf(RequestFilter.class);
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
}
