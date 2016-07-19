package com.ee.imperator.api;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.request.context.PageContext;

public class LongPolling {
	private static final Logger LOG = LogManager.createLogger();

	LongPolling() {
	}

	public Response handle(PageContext context) {
		Map<String, String> arguments = getArguments(context);
		if(arguments != null) {
			if("update".equals(arguments.get("mode"))) {
				sleep(arguments);
			}
			try {
				JSONObject response = Api.handleRequest(arguments, context.getUser());
				return Response.ok(response == null ? null : response.toString()).type(MediaType.APPLICATION_JSON_TYPE).build();
			} catch (RequestException e) {
				return Response.status(e.getStatus()).type(MediaType.APPLICATION_JSON_TYPE).entity(e.getMessage(context.getUser().getLanguage())).build();
			}
		}
		return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).build();
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
			int maxTries = Imperator.getConfig().getInt(getClass(), "maxTries");
			long sleep = Imperator.getConfig().getLong(getClass(), "sleep");
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
		return !Imperator.getState().hasChatMessages(gid, time) && (!game || Imperator.getState().getGame(gid).getTime() <= time);
	}

	private Map<String, String> getArguments(PageContext context) {
		MultivaluedMap<String, String> params = context.getPostParams();
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
