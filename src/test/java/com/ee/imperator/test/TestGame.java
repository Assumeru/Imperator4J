package com.ee.imperator.test;

import java.util.HashMap;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ee.imperator.Imperator;
import com.ee.imperator.api.TestApi;
import com.ee.imperator.game.Cards;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Map;
import com.ee.imperator.map.Region;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Player;

public class TestGame {
	private static final Logger LOG = LogManager.createLogger();
	private static final int[] CARD_COMBINATIONS = {10, 8, 6, 4};
	private Game game;
	private Player player1;
	private Player player2;
	private Player player3;
	private java.util.Map<Player, Territory> territories = new HashMap<>();

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
		testCombat();
		testCards();
		testForfeit();
		testGameOver();
	}

	private void testGameOver() {
		Assert.assertEquals(Game.State.FINISHED, game.getState());
		for(Player player : game.getPlayers()) {
			if(player.getState() == Player.State.GAME_OVER) {
				Assert.assertEquals(-1, player.getMember().getScore());
				Assert.assertEquals(1, player.getMember().getLosses());
			} else if(player.getState() == Player.State.VICTORIOUS) {
				Assert.assertEquals(game.getPlayers().size() - 1, player.getMember().getScore());
				Assert.assertEquals(1, player.getMember().getWins());
			} else {
				Assert.fail("Unexpected state " + player.getState());
			}
		}
	}

	private void testForfeit() {
		Player loser = game.getCurrentPlayer();
		forfeit(loser);
		Assert.assertNotEquals(loser, game.getCurrentPlayer());
		Assert.assertEquals(Player.State.GAME_OVER, loser.getState());
	}

	private void testCards() {
		Territory target = null;
		for(Player player : game.getPlayers()) {
			if(player.getState() == Player.State.GAME_OVER) {
				target = territories.get(player);
			}
		}
		while(game.getCurrentPlayer().getCards().size() < Cards.MAX_CARDS) {
			Player player = game.getCurrentPlayer();
			Territory territory = territories.get(player);
			if(target.getOwner().equals(player) || territory.getUnits() <= Game.MAX_ATTACKERS) {
				fortify(player);
				stack(player, territory, game.getUnits());
			} else {
				attack(player, territory, target, Game.MAX_ATTACKERS, 1);
			}
			endTurn();
		}
		Player player = game.getCurrentPlayer();
		Assert.assertEquals(Cards.MAX_CARDS, player.getCards().size());
		boolean playedCards = false;
		int units = game.getUnits();
		for(int combo : CARD_COMBINATIONS) {
			if(player.getCards().canPlay(combo)) {
				playCards(player, combo);
				Assert.assertNotEquals(units, game.getUnits());
				playedCards = true;
			}
		}
		Assert.assertTrue("No card combination could be played", playedCards);
		stack(player, territories.get(player), game.getUnits());
		Assert.assertEquals(Game.State.TURN_START, game.getState());
	}

	private void testCombat() {
		Region region = game.getMap().getRegions().values().iterator().next();
		Territory winner = region.getTerritories().get(0);
		Territory loser = region.getTerritories().get(1);
		Player owner = loser.getOwner();
		try {
			startMove();
			Assert.assertEquals(Game.State.TURN_START, game.getState());
		} catch(Exception e) {
			LOG.i("Couldn't move before attacking");
		}
		LOG.i(owner.getId() + " has been selected to lose the game");
		while(!loser.getOwner().equals(winner.getOwner())) {
			Player player = game.getCurrentPlayer();
			if(player.equals(winner.getOwner())) {
				if(winner.getUnits() > Game.MAX_ATTACKERS) {
					attack(player, winner, loser, Game.MAX_ATTACKERS, winner.getUnits() - 1);
				} else {
					fortify(player);
					stack(player, winner, game.getUnits());
				}
			} else if(!player.equals(loser.getOwner()) && loser.getUnits() > Game.MAX_DEFENDERS) {
				Territory territory = territories.get(player);
				if(territory.getUnits() > Game.MAX_ATTACKERS) {
					attack(player, territory, loser, Game.MAX_ATTACKERS, 1);
				} else {
					fortify(player);
					stack(player, territory, game.getUnits());
				}
			}
			if(loser.getOwner().equals(winner.getOwner())) {
				Assert.assertTrue(game.hasConquered());
				startMove();
				Assert.assertEquals(Game.State.POST_COMBAT, game.getState());
				Assert.assertEquals(Game.MAX_MOVE_UNITS, game.getUnits());
				while(game.getUnits() > 0 && loser.getUnits() > 1) {
					moveUnits(winner.getOwner(), loser, winner, 1);
				}
				try {
					moveUnits(winner.getOwner(), loser, winner, 1);
					Assert.fail("Managed to move more units");
				} catch(Exception e) {
					LOG.i("Couldn't move more units");
				}
			}
			endTurn();
		}
		Assert.assertTrue("Region not conquered by " + winner.getOwner().getId(), region.isOwnedBy(winner.getOwner()));
		Assert.assertEquals(Player.State.GAME_OVER, owner.getState());
		Assert.assertEquals(1, winner.getOwner().getCards().size());
		while(!game.getCurrentPlayer().equals(winner.getOwner())) {
			Assert.assertNotEquals(owner, game.getCurrentPlayer());
			endTurn();
		}
		Assert.assertEquals(region.getUnits(), game.getUnits());
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
			territories.put(t.getOwner(), t);
		}
		Assert.assertTrue("Not every player has an equal amount of territories", p1 == p2 && p2 == p3 && p1 > 0);
	}

	private void testStack() {
		Territory territory = territories.get(player1);
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
		}
		try {
			attack(player1, territory, territory.getBorders().get(0), 3, 3);
			Assert.assertEquals(Game.State.FORTIFY, game.getState());
		} catch(Exception e) {
			LOG.i("Could not attack after fortifying");
		}
		endTurn();
		fortify(player2);
		stack(player2, territories.get(player2), game.getUnits());
		endTurn();
		fortify(player3);
		stack(player3, territories.get(player3), game.getUnits());
		endTurn();
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
			endTurn();
		}
	}

	private void endTurn() {
		Player player = game.getCurrentPlayer();
		TestApi.INSTANCE.handle(new TestApi.EndTurn(player, null));
		LOG.i(player.getId() + " ended turn");
	}

	private void stack(Player player, Territory territory, int units) {
		TestApi.INSTANCE.handle(new TestApi.Stack(player, territory, units));
		LOG.i("Placed " + units + " units in " + territory.getId());
	}

	private void fortify(Player player) {
		TestApi.INSTANCE.handle(new TestApi.Fortify(player));
		LOG.i(player.getId() + " fortified");
	}

	private void attack(Player player, Territory from, Territory to, int units, int move) {
		TestApi.INSTANCE.handle(new TestApi.Attack(player, from, to, units, move));
		LOG.i(from.getId() + " attacked " + to.getId());
	}

	private void startMove() {
		TestApi.INSTANCE.handle(new TestApi.StartMove(game.getCurrentPlayer()));
		LOG.i("started moving units");
	}

	private void moveUnits(Player player, Territory from, Territory to, int move) {
		TestApi.INSTANCE.handle(new TestApi.MoveUnits(player, from, to, move));
		LOG.i(move + " moved from " + from.getId() + " to " + to.getId());
	}

	private void playCards(Player player, int combo) {
		TestApi.INSTANCE.handle(new TestApi.PlayCards(player, combo));
		LOG.i(player.getId() + " played cards for " + combo + " units");
	}

	private void forfeit(Player player) {
		TestApi.INSTANCE.handle(new TestApi.Forfeit(player));
		LOG.i(player.getId() + " forfeited");
	}
}
