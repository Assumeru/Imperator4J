package com.ee.imperator.api;

import java.util.HashMap;
import java.util.Map;

import org.ee.collection.ListMap;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.Status;
import org.ee.web.request.Request;
import org.ee.web.response.Response;
import org.ee.web.response.SimpleResponse;
import org.json.JSONObject;

import com.ee.imperator.exception.RequestException;
import com.ee.imperator.user.Member;

public class LongPolling extends ApiImplementation {
	private static final Logger LOG = LogManager.createLogger();

	LongPolling(Api api) {
		super(api);
	}

	public org.ee.web.response.Response handle(Request request) {
		Map<String, String> arguments = getArguments(request);
		if(arguments != null) {
			if("update".equals(arguments.get("mode"))) {
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
			int maxTries = api.getContext().getConfig().getInt(getClass(), "maxTries");
			long sleep = api.getContext().getConfig().getLong(getClass(), "sleep");
			for(int i = 0; shouldSleep(gid, time, game) && i < maxTries || maxTries == 0; i++) {
				Thread.sleep(sleep);
			}
		} catch(NumberFormatException e) {
			// Do nothing
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
