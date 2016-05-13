package com.ee.imperator.data;

import java.io.IOException;
import java.util.List;

import org.ee.web.request.Request;

import com.ee.imperator.chat.ChatMessage;
import com.ee.imperator.game.Attack;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.Cards.Card;
import com.ee.imperator.game.Game.State;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.map.Map;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

public class JoinedDataProvider implements DataProvider {
	private final GameProvider gameProvider;
	private final MemberProvider memberProvider;
	private final MapProvider mapProvider;
	private final ChatProvider chatProvider;

	public JoinedDataProvider(GameProvider gameProvider, MemberProvider memberProvider, MapProvider mapProvider, ChatProvider chatProvider) {
		this.gameProvider = gameProvider;
		this.memberProvider = memberProvider;
		this.mapProvider = mapProvider;
		this.chatProvider = chatProvider;
	}

	@Override
	public List<Game> getGames() {
		return gameProvider.getGames();
	}

	@Override
	public Game getGame(int id) {
		return gameProvider.getGame(id);
	}

	@Override
	public Game createGame(Player owner, Map map, String name, String password) {
		return gameProvider.createGame(owner, map, name, password);
	}

	@Override
	public boolean addPlayerToGame(Player player, Game game) {
		return gameProvider.addPlayerToGame(player, game);
	}

	@Override
	public boolean removePlayerFromGame(Player player, Game game) {
		return gameProvider.removePlayerFromGame(player, game);
	}

	@Override
	public boolean deleteGame(Game game) {
		return gameProvider.deleteGame(game);
	}

	@Override
	public void startGame(Game game) {
		gameProvider.startGame(game);
	}

	@Override
	public void updateGameTime(Game game) {
		gameProvider.updateGameTime(game);
	}

	@Override
	public List<LogEntry> getCombatLogs(Game game, long time) {
		return gameProvider.getCombatLogs(game, time);
	}

	@Override
	public void setAutoRoll(Player player, boolean autoroll) {
		gameProvider.setAutoRoll(player, autoroll);
	}

	@Override
	public boolean addCards(Player player, Card card, int amount) {
		return gameProvider.addCards(player, card, amount);
	}

	@Override
	public void startTurn(Player player) {
		gameProvider.startTurn(player);
	}

	@Override
	public void updateUnitsAndState(Game game, State state, int units) {
		gameProvider.updateUnitsAndState(game, state, units);
	}

	@Override
	public void placeUnits(Game game, Territory territory, int units) {
		gameProvider.placeUnits(game, territory, units);
	}

	@Override
	public void forfeit(Player player) {
		gameProvider.forfeit(player);
	}

	@Override
	public void saveAttack(Game game, Attack attack) {
		gameProvider.saveAttack(game, attack);
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
	public Integer getId(Request request) {
		return memberProvider.getId(request);
	}

	@Override
	public List<Map> getMaps() {
		return mapProvider.getMaps();
	}

	@Override
	public Map getMap(int id) {
		return mapProvider.getMap(id);
	}

	@Override
	public void close() throws IOException {
		try {
			gameProvider.close();
		} finally {
			try {
				mapProvider.close();
			} finally {
				memberProvider.close();
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
	public boolean addMessage(ChatMessage message) {
		return chatProvider.addMessage(message);
	}

	@Override
	public boolean deleteMessage(int gid, long time) {
		return chatProvider.deleteMessage(gid, time);
	}
}
