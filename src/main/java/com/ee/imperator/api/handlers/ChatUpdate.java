package com.ee.imperator.api.handlers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.chat.ChatMessage;
import com.ee.imperator.exception.InvalidRequestException;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;
import com.ee.imperator.user.User;

@Request(mode = "update", type = "chat")
public class ChatUpdate {
	public JSONObject handle(Member member, @Param("gid") int gid, @Param("time") long time) throws InvalidRequestException {
		if(!canUseChat(member, gid)) {
			throw new InvalidRequestException(member.getId() + " cannot use chat " + gid, "update", "chat");
		}
		return new JSONObject().put("update", System.currentTimeMillis()).put("messages", getMessages(gid, time));
	}

	private boolean canUseChat(User user, int gid) {
		return gid == 0 || GameUpdate.playerInGame(user, gid);
	}

	static JSONArray getMessages(int gid, long time) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
		JSONArray out = new JSONArray();
		for(ChatMessage message : Imperator.getData().getChatMessages(gid, time)) {
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
