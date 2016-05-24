package com.ee.imperator.test.state;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.ee.imperator.data.GameState;
import com.ee.imperator.game.Attack;
import com.ee.imperator.game.Cards;
import com.ee.imperator.game.Cards.Card;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.Game.State;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.map.Map;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Player;
import com.ee.imperator.user.User;

public class MemoryGameState implements GameState {
	private final java.util.Map<Integer, Game> games = new HashMap<>();
	private int id = 1;

	@Override
	public void close() throws IOException {
	}

	@Override
	public List<Game> getGames() {
		return new ArrayList<>(games.values());
	}

	@Override
	public List<Game> getGames(User user) {
		return Collections.emptyList();
	}

	@Override
	public Game getGame(int id) {
		return games.get(id);
	}

	@Override
	public Game createGame(Player owner, Map map, String name, String password) {
		return new Game(id++, map.clone(), name, owner, password, System.currentTimeMillis());
	}

	@Override
	public boolean addPlayerToGame(Player player, Game game) {
		game.setTime(System.currentTimeMillis());
		game.addPlayer(player);
		return true;
	}

	@Override
	public boolean removePlayerFromGame(Player player, Game game) {
		game.removePlayer(player);
		game.setTime(System.currentTimeMillis());
		return true;
	}

	@Override
	public boolean deleteGame(Game game) {
		games.remove(game.getId());
		return true;
	}

	@Override
	public void startGame(Game game) {
	}

	@Override
	public List<LogEntry> getCombatLogs(Game game, long time) {
		return Collections.emptyList();
	}

	@Override
	public void setAutoRoll(Player player, boolean autoroll) {
		player.setAutoRoll(autoroll);
	}

	@Override
	public boolean addCards(Player player, Card card, int amount) {
		while(amount > 0) {
			player.getCards().add(card);
			amount--;
		}
		while(amount < 0) {
			player.getCards().remove(card);
			amount++;
		}
		return true;
	}

	@Override
	public void startTurn(Player player) {
		Game game = player.getGame();
		game.setTime(System.currentTimeMillis());
		game.setCurrentTurn(player);
		game.setConquered(false);
		game.setState(Game.State.TURN_START);
		game.setUnits(player.getUnitsFromRegionsPerTurn());
	}

	@Override
	public void updateUnitsAndState(Game game, State state, int units) {
		game.setUnits(game.getUnits() + units);
		game.setState(state);
		game.setTime(System.currentTimeMillis());
	}

	@Override
	public void placeUnits(Game game, Territory territory, int units) {
		game.setUnits(game.getUnits() - units);
		game.setTime(System.currentTimeMillis());
		territory.setUnits(territory.getUnits() + units);
	}

	@Override
	public void forfeit(Player player) {
		player.setState(Player.State.GAME_OVER);
		player.setAutoRoll(true);
	}

	@Override
	public void saveAttack(Game game, Attack attack) {
		game.getAttacks().add(attack);
	}

	@Override
	public void deleteAttack(Attack attack) {
		attack.getAttacker().getOwner().getGame().getAttacks().remove(attack);
	}

	@Override
	public void attack(Game game, Attack attack) {
		int attackerUnits = attack.getAttacker().getUnits() - attack.getAttackLosses();
		int defenderUnits = attack.getDefender().getUnits() - attack.getDefendLosses();
		Player dOwner = attack.getDefender().getOwner();
		boolean conquered = defenderUnits < 1;
		if(conquered) {
			dOwner = attack.getAttacker().getOwner();
			int move = attack.getMove();
			if(move >= attackerUnits) {
				move = attackerUnits - 1;
			}
			attackerUnits -= move;
			defenderUnits = move;
		}
		attack.getAttacker().setUnits(attackerUnits);
		attack.getDefender().setUnits(defenderUnits);
		attack.getDefender().setOwner(dOwner);
		game.setConquered(game.hasConquered() || conquered);
		game.setTime(System.currentTimeMillis());
	}

	@Override
	public void setState(Player player, com.ee.imperator.user.Player.State state) {
		player.setState(state);
	}

	@Override
	public void saveMissions(Game game) {
	}

	@Override
	public void setState(Game game, State state) {
		game.setState(state);
		game.setTime(System.currentTimeMillis());
	}

	@Override
	public void moveUnits(Game game, Territory from, Territory to, int move) {
		game.setUnits(game.getUnits() - move);
		from.setUnits(from.getUnits() - move);
		to.setUnits(to.getUnits() + move);
	}

	@Override
	public void playCards(Player player, int units) {
		Cards combo = player.getCards().getCombination(units);
		player.getCards().removeAll(combo);
		player.getGame().setUnits(player.getGame().getUnits() + units);
		player.getGame().setTime(System.currentTimeMillis());
	}

	@Override
	public boolean victory(Player player) {
		Game game = player.getGame();
		game.setState(Game.State.FINISHED);
		game.setTime(System.currentTimeMillis());
		game.setCurrentTurn(null);
		player.setState(Player.State.VICTORIOUS);
		return true;
	}

}
