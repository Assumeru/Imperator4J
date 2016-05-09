package com.ee.imperator.data;

import java.util.List;

import com.ee.imperator.chat.ChatMessage;

public interface ChatProvider {
	List<ChatMessage> getChatMessages(int id, long time);

	boolean hasChatMessages(int id, long time);

	boolean addMessage(ChatMessage message);

	boolean deleteMessage(int gid, long time);
}
