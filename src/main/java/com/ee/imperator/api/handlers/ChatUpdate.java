package com.ee.imperator.api.handlers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.api.Api;
import com.ee.imperator.api.handlers.Endpoint.Mode;
import com.ee.imperator.chat.ChatMessage;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;
import com.ee.imperator.user.User;

@Endpoint(mode = Mode.UPDATE, type = "chat")
public class ChatUpdate {
	private final ImperatorApplicationContext context;

	public ChatUpdate(ImperatorApplicationContext context) {
		this.context = context;
	}

	public JSONObject handle(Member member, @Param("time") long time, @Param("gid") int gid) throws InvalidRequestException {
		if(member.isGuest() || !canUseChat(member, gid, context)) {
			throw new InvalidRequestException(member.getId() + " cannot use chat", Mode.UPDATE, "chat");
		}
		return new JSONObject().put("update", System.currentTimeMillis()).put("messages", getMessages(gid, time, context));
	}

	static boolean canUseChat(User user, int gid, ImperatorApplicationContext context) {
		return gid == 0 || GameUpdate.playerInGame(user, gid, context);
	}

	static JSONArray getMessages(int gid, long time, ImperatorApplicationContext context) {
		DateFormat format = new SimpleDateFormat(Api.DATE_ATOM, Locale.US);
		JSONArray out = new JSONArray();
		for(ChatMessage message : context.getState().getChatMessages(gid, time)) {
			JSONObject user = new JSONObject()
					.put("id", message.getUser().getId())
					.put("name", message.getUser().getName());
			if(message.getUser() instanceof Player) {
				user.put("color", ((Player) message.getUser()).getColor());
			}
			JSONObject msg = new JSONObject()
					.put("message", message.getMessage())
					.put("time", format.format(new Date(message.getTime())))
					.put("timestamp", message.getTime())
					.put("user", user);
			out.put(msg);
		}
		return out;
	}
}
