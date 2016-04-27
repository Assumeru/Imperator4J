package com.ee.imperator.data;

import java.io.IOException;
import java.util.List;

import org.ee.web.request.Request;

import com.ee.imperator.game.Game;
import com.ee.imperator.map.Map;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

public class JoinedDataProvider implements DataProvider {
	private final GameProvider gameProvider;
	private final MemberProvider memberProvider;
	private final MapProvider mapProvider;

	public JoinedDataProvider(GameProvider gameProvider, MemberProvider memberProvider, MapProvider mapProvider) {
		this.gameProvider = gameProvider;
		this.memberProvider = memberProvider;
		this.mapProvider = mapProvider;
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
}
