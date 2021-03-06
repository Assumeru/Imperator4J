package com.ee.imperator.data;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.ee.web.request.Request;

import com.ee.imperator.chat.ChatMessage;
import com.ee.imperator.data.transaction.GameTransaction;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.map.Map;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;
import com.ee.imperator.user.User;

public class JoinedState implements State {
	private final GameState gameProvider;
	private final MemberState memberProvider;
	private final ChatState chatProvider;

	public JoinedState(GameState gameProvider, MemberState memberProvider, ChatState chatProvider) {
		this.gameProvider = gameProvider;
		this.memberProvider = memberProvider;
		this.chatProvider = chatProvider;
	}

	@Override
	public List<Game> getGames() {
		return gameProvider.getGames();
	}

	@Override
	public List<Game> getGames(User user) {
		return gameProvider.getGames(user);
	}

	@Override
	public Game getGame(int id) {
		return gameProvider.getGame(id);
	}

	@Override
	public Game createGame(Player owner, Map map, String name, String password) throws TransactionException {
		return gameProvider.createGame(owner, map, name, password);
	}

	@Override
	public void deleteGame(Game game) throws TransactionException {
		gameProvider.deleteGame(game);
	}

	@Override
	public List<LogEntry> getCombatLogs(Game game, long time) {
		return gameProvider.getCombatLogs(game, time);
	}

	@Override
	public GameTransaction modify(Game game) throws TransactionException {
		return gameProvider.modify(game);
	}

	@Override
	public Collection<Integer> deleteOldGames(long finishedTime, long time) {
		return gameProvider.deleteOldGames(finishedTime, time);
	}

	@Override
	public Member getMember(int id) {
		return memberProvider.getMember(id);
	}

	@Override
	public Member getMember(Request request) {
		return memberProvider.getMember(request);
	}

	@Override
	public int getId(Request request) {
		return memberProvider.getId(request);
	}

	@Override
	public List<Member> getMembers() {
		return memberProvider.getMembers();
	}

	@Override
	public void close() throws IOException {
		try {
			gameProvider.close();
		} finally {
			try {
				memberProvider.close();
			} finally {
				chatProvider.close();
			}
		}
	}

	@Override
	public List<ChatMessage> getChatMessages(int id, long time) {
		return chatProvider.getChatMessages(id, time);
	}

	@Override
	public boolean hasChatMessages(int id, long time) {
		return chatProvider.hasChatMessages(id, time);
	}

	@Override
	public void addMessage(ChatMessage message) throws TransactionException {
		chatProvider.addMessage(message);
	}

	@Override
	public void deleteMessage(int gid, long time) throws TransactionException {
		chatProvider.deleteMessage(gid, time);
	}

	@Override
	public int deleteOldMessages(long time, int keep) {
		return chatProvider.deleteOldMessages(time, keep);
	}
}
