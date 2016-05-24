package com.ee.imperator.test;

import org.junit.Before;
import org.junit.Test;

import com.ee.imperator.Imperator;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Map;
import com.ee.imperator.user.Player;

public class TestGame {
	private Game game;

	@Before
	public void init() {
		System.setProperty("com.ee.imperator.Config", Config.class.getName());
		new Imperator(null);
	}

	@Test
	public void test() {
		createGame();
		addPlayers();
		Imperator.getState().startGame(game);
		//TODO
	}

	private void createGame() {
		Player owner = new Player(Imperator.getState().getMember(1));
		owner.setColor("FF0000");
		Map map = Imperator.getMapProvider().getMap(0);
		game = Imperator.getState().createGame(owner, map, "Test game", null);
	}

	private void addPlayers() {
		Player player2 = new Player(Imperator.getState().getMember(2));
		player2.setColor("00FF00");
		Player player3 = new Player(Imperator.getState().getMember(3));
		player2.setColor("0000FF");
		Imperator.getState().addPlayerToGame(player2, game);
		Imperator.getState().addPlayerToGame(player3, game);
	}
}
