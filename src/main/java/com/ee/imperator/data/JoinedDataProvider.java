package com.ee.imperator.data;

import java.util.List;

import org.ee.web.request.Request;

import com.ee.imperator.game.Game;
import com.ee.imperator.map.Map;
import com.ee.imperator.user.Member;

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
	public Member getMember(int id) {
		return memberProvider.getMember(id);
	}

	@Override
	public Member getMember(Request request) {
		return memberProvider.getMember(request);
	}

	@Override
	public List<Map> getMaps() {
		return mapProvider.getMaps();
	}

	@Override
	public Map getMap(int id) {
		return mapProvider.getMap(id);
	}
}
