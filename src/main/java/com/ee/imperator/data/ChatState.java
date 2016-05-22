package com.ee.imperator.data;

import java.io.Closeable;
import java.util.List;

import com.ee.imperator.chat.ChatMessage;

public interface ChatState extends Closeable {
	List<ChatMessage> getChatMessages(int id, long time);

	boolean hasChatMessages(int id, long time);

	boolean addMessage(ChatMessage message);

	boolean deleteMessage(int gid, long time);
}
