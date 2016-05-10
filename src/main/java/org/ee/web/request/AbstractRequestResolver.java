package org.ee.web.request;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriInfo;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.request.page.Ignore;
import org.ee.web.request.page.NavigationPage;
import org.ee.web.request.page.WebPage;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

public abstract class AbstractRequestResolver {
	private static final Logger LOG = LogManager.createLogger();
	private static final Object MUTEX = new Object();
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
	public Response handleRequest(@Context UriInfo uriInfo, @Context HttpHeaders headers) {
		String path = getPath();
		RequestHandler page = getHandler(path);
		return handleRequest(page, path, headers.getCookies(), uriInfo.getQueryParameters(), null);
	}

	@Path("/{path: (.*)}")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	public Response handleRequest(@Context UriInfo uriInfo, @Context HttpHeaders headers, MultivaluedMap<String, String> postParams) {
		String path = getPath();
		RequestHandler page = getHandler(path);
		return handleRequest(page, path, headers.getCookies(), uriInfo.getQueryParameters(), postParams);
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

	private Response handleRequest(final RequestHandler handler, final String path, Map<String, Cookie> cookies, MultivaluedMap<String, String> getParams, MultivaluedMap<String, String> postParams) {
		final Request request = new Request(servletContext, this.request, cookies, getParams, postParams, path);
		final Object context = createContext(request);
		request.setContext(context);
		try {
			return handler.getResponse(request);
		} catch(WebApplicationException e) {
			RequestHandler errorHandler = getStatusPage(e.getResponse().getStatus());
			if(errorHandler != null) {
				return errorHandler.getResponse(request);
			}
			if(e.getResponse().getStatusInfo().getFamily() == Family.REDIRECTION) {
				LOG.v("Redirecting");
			} else {
				LOG.w("Returning WebApplicationException response", e);
			}
			return e.getResponse();
		} catch (Exception e) {
			LOG.e("Error handling request using " + handler, e);
		}
		return getStatusPageInternal(Status.INTERNAL_SERVER_ERROR).getResponse(request);
	}

	private RequestHandler getStatusPageInternal(final int status) {
		RequestHandler handler = getStatusPage(status);
		if(handler != null) {
			return handler;
		}
		LOG.w("No status page found for " + status);
		return new DefaultRequestHandler(status);
	}

	private RequestHandler getStatusPageInternal(Status status) {
		return getStatusPageInternal(status.getStatusCode());
	}

	protected abstract RequestHandler getStatusPage(int status);

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
			synchronized(MUTEX) {
				initRequestHandlers();
			}
		}
		if(path.contains("../")) {
			//Is this even possible?
			LOG.e("Path contains ../");
			return getStatusPageInternal(Status.BAD_REQUEST);
		}
		for(RequestHandler handler : requestHandlers) {
			if(handler.matches(path)) {
				return handler;
			}
		}
		LOG.i("No handler found for " + path);
		return getStatusPageInternal(Status.NOT_FOUND);
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
