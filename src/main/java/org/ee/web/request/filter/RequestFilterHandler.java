package org.ee.web.request.filter;

import java.util.Set;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.Status;
import org.ee.web.Status.Family;
import org.ee.web.exception.WebException;
import org.ee.web.request.DefaultRequestHandler;
import org.ee.web.request.Request;
import org.ee.web.request.RequestHandler;
import org.ee.web.response.Response;

public abstract class RequestFilterHandler implements RequestHandler {
	private static final Logger LOG = LogManager.createLogger();
	private final Set<RequestFilter> filters;

	public RequestFilterHandler(Set<RequestFilter> filters) {
		this.filters = filters;
	}

	@Override
	public Response handle(Request request) {
		RequestHandler handler = getMatchingHandler(request);
		try {
			return handler.handle(request);
		} catch(WebException e) {
			RequestHandler errorHandler = getStatusPage(e.getResponse().getStatus());
			if(errorHandler != null) {
				return errorHandler.handle(request);
			}
			if(e.getResponse().getStatus().getFamily() == Family.REDIRECTION) {
				LOG.v("Redirecting");
			} else {
				LOG.w("Returning WebException response", e);
			}
			return e.getResponse();
		} catch(Exception e) {
			LOG.e("Error handling request using " + handler, e);
		}
		return getStatusPageInternal(Status.INTERNAL_SERVER_ERROR).handle(request);
	}

	private RequestHandler getMatchingHandler(Request request) {
		String path = request.getPath();
		if(path.contains("../")) {
			//Is this even possible?
			LOG.e("Path contains ../");
			return getStatusPageInternal(Status.BAD_REQUEST);
		}
		for(RequestFilter filter : filters) {
			if(filter.matches(request)) {
				return filter;
			}
		}
		LOG.i("No handler found for " + path);
		return getStatusPageInternal(Status.NOT_FOUND);
	}

	private RequestHandler getStatusPageInternal(Status status) {
		RequestHandler handler = getStatusPage(status);
		if(handler != null) {
			return handler;
		}
		LOG.w("No status page found for " + status);
		return new DefaultRequestHandler(status);
	}

	protected abstract RequestHandler getStatusPage(Status status);

	protected Set<RequestFilter> getFilters() {
		return filters;
	}
}
