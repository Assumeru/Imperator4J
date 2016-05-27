package com.ee.imperator.test;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ee.imperator.Imperator;
import com.ee.imperator.api.TestApi;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Map;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Player;

public class TestGame {
	private static final Logger LOG = LogManager.createLogger();
	private Game game;
	private Player player1;
	private Player player2;
	private Player player3;

	@Before
	public void init() {
		System.setProperty("com.ee.imperator.Config", Config.class.getName());
		new Imperator(null);
		createGame();
		addPlayers();
		Imperator.getState().startGame(game);
		skipTo(player1);
	}

	@Test
	public void test() {
		testTerritories();
		Assert.assertEquals(Game.State.TURN_START, game.getState());
		testStack();
		//TODO
	}

	private void testTerritories() {
		int p1 = 0;
		int p2 = 0;
		int p3 = 0;
		for(Territory t : game.getMap().getTerritories().values()) {
			Assert.assertEquals(Game.INITIAL_UNITS, t.getUnits());
			if(t.getOwner().equals(player1)) {
				p1++;
			} else if(t.getOwner().equals(player2)) {
				p2++;
			} else if(t.getOwner().equals(player3)) {
				p3++;
			}
		}
		Assert.assertTrue("Not every player has an equal amount of territories", p1 == p2 && p2 == p3 && p1 > 0);
	}

	private void testStack() {
		Territory territory = null;
		for(Territory t : game.getMap().getTerritories().values()) {
			if(t.getOwner().equals(player1)) {
				territory = t;
				break;
			}
		}
		LOG.i("Player 1 owns " + territory.getId());
		int units = territory.getUnits();
		try {
			stack(player1, territory, 1);
			Assert.assertEquals(units, territory.getUnits());
		} catch(Exception e) {
			LOG.i("Could not stack before fortifying");
		}
		try {
			fortify(player2);
			Assert.assertEquals(Game.State.TURN_START, game.getState());
		} catch(Exception e) {
			LOG.i("Could not fortify off turn");
		}
		fortify(player1);
		Assert.assertEquals(Game.State.FORTIFY, game.getState());
		Assert.assertEquals(Game.MIN_FORTIFY, game.getUnits());
		int gameUnits = game.getUnits();
		while(game.getUnits() > 0) {
			stack(player1, territory, 1);
			Assert.assertEquals(++units, territory.getUnits());
			Assert.assertEquals(--gameUnits, game.getUnits());
			LOG.i("Placed 1 unit in " + territory.getId());
		}
		try {
			attack(player1, territory, territory.getBorders().get(0), 3, 3);
			Assert.assertEquals(Game.State.FORTIFY, game.getState());
		} catch(Exception e) {
			LOG.i("Could not attack after fortifying");
		}
	}

	private void createGame() {
		player1 = new Player(Imperator.getState().getMember(1));
		player1.setColor("FF0000");
		Map map = Imperator.getMapProvider().getMap(0);
		game = Imperator.getState().createGame(player1, map, "Test game", null);
	}

	private void addPlayers() {
		player2 = new Player(Imperator.getState().getMember(2));
		player2.setColor("00FF00");
		player3 = new Player(Imperator.getState().getMember(3));
		player2.setColor("0000FF");
		Imperator.getState().addPlayerToGame(player2, game);
		Imperator.getState().addPlayerToGame(player3, game);
	}

	private void skipTo(Player player) {
		while(!game.getCurrentPlayer().equals(player)) {
			LOG.i("Skipping turn for " + game.getCurrentPlayer().getId());
			TestApi.INSTANCE.handle(new TestApi.EndTurn(game.getCurrentPlayer(), null));
		}
	}

	private void stack(Player player, Territory territory, int units) {
		TestApi.INSTANCE.handle(new TestApi.Stack(player, territory, units));
	}

	private void fortify(Player player) {
		TestApi.INSTANCE.handle(new TestApi.Fortify(player));
	}

	private void attack(Player player, Territory from, Territory to, int units, int move) {
		TestApi.INSTANCE.handle(new TestApi.Attack(player, from, to, units, move));
	}
}
