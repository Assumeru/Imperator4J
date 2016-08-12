package com.ee.imperator.api;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.ee.collection.ListMap;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.Status;
import org.ee.web.request.Request;
import org.ee.web.response.Response;
import org.ee.web.response.SimpleResponse;
import org.json.JSONObject;

import com.ee.imperator.api.handlers.Endpoint;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.user.Member;

/**
 * Provides a way of accessing the API through HTTP requests.
 */
public class LongPolling extends ApiImplementation {
	private static final Logger LOG = LogManager.createLogger();
	private final int maxTries;
	private final long sleep;

	LongPolling(Api api) {
		super(api);
		maxTries = api.getContext().getIntSetting(getClass(), "maxTries");
		sleep = api.getContext().getLongSetting(getClass(), "sleep");
	}

	/**
	 * Handles a request by sending its POST parameters to the API.
	 * If the request mode is {@code update} the thread will wait until there is data to send or the configured time limit has been exceeded.
	 * 
	 * @param request The request to handle
	 * @return A response to send to the user
	 */
	public Response handle(Request request) {
		Map<String, String> arguments = getArguments(request);
		if(arguments != null) {
			Endpoint.Mode mode = Endpoint.Mode.of(arguments.get("mode"));
			if(!api.getContext().getCsrfTokenBuilder().tokenIsValid(request)) {
				return getResponse(Status.FORBIDDEN, Api.getErrorMessage("CSRF token mismatch", mode, arguments.get("type")));
			} else if(mode == Endpoint.Mode.UPDATE) {
				sleep(arguments);
			}
			Member user = api.getContext().getState().getMember(request);
			try {
				JSONObject response = handleRequest(arguments, user);
				String responseString = response == null ? null : response.toString();
				return getResponse(responseString == null || responseString.isEmpty() ? Status.NO_CONTENT : Status.OK, responseString);
			} catch (RequestException e) {
				return getResponse(e.getStatus(), e.getMessage(user.getLanguage()));
			}
		}
		return getResponse(Status.BAD_REQUEST, null);
	}

	private Response getResponse(Status status, Object output) {
		Response response = new SimpleResponse(status, output);
		response.setContentType("application", "json");
		return response;
	}

	private void sleep(Map<String, String> arguments) {
		try {
			String type = arguments.get("type");
			boolean game = false;
			if("game".equals(type) || "pregame".equals(type)) {
				game = true;
			} else if(!"chat".equals(type)) {
				return;
			}
			long time = Long.parseLong(arguments.get("time"));
			int gid = Integer.parseInt(arguments.get("gid"));
			for(int i = 0; shouldSleep(gid, time, game) && i < maxTries || maxTries == 0; i++) {
				Thread.sleep(sleep);
			}
		} catch(NumberFormatException e) {
			LOG.log(Level.ALL, e);
		} catch(InterruptedException e) {
			LOG.e(e);
		}
	}

	private boolean shouldSleep(int gid, long time, boolean game) {
		return !api.getContext().getState().hasChatMessages(gid, time) && (!game || api.getContext().getState().getGame(gid).getTime() <= time);
	}

	private Map<String, String> getArguments(Request context) {
		ListMap<String, String> params = context.getPostParameters();
		if(params == null || params.isEmpty()) {
			return null;
		}
		Map<String, String> output = new HashMap<>();
		for(String key : params.keySet()) {
			output.put(key, params.getFirst(key));
		}
		return output;
	}
}
