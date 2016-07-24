package com.ee.imperator.test.state;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.ee.imperator.chat.ChatMessage;
import com.ee.imperator.data.ChatState;

public class DummyChatState implements ChatState {
	@Override
	public void close() throws IOException {
	}

	@Override
	public List<ChatMessage> getChatMessages(int id, long time) {
		return Collections.emptyList();
	}

	@Override
	public boolean hasChatMessages(int id, long time) {
		return false;
	}

	@Override
	public void addMessage(ChatMessage message) {
	}

	@Override
	public void deleteMessage(int gid, long time) {
	}

	@Override
	public int deleteOldMessages(long time, int keep) {
		return 0;
	}
}
