package com.ee.imperator.api.handlers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.api.Api;
import com.ee.imperator.game.Attack;
import com.ee.imperator.game.Cards;
import com.ee.imperator.game.Game;
import com.ee.imperator.game.log.LogEntry;
import com.ee.imperator.map.Region;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;
import com.ee.imperator.user.User;

@Request(mode = "update", type = "game")
public class GameUpdate {
	public JSONObject handle(Member member, @Param("gid") int gid, @Param("time") long time) {
		Game game = Imperator.getData().getGame(gid);
		JSONObject output = new JSONObject()
				.put("update", System.currentTimeMillis());
		if(game == null) {
			output.put("gameState", member.getLanguage().translate("This game has been disbanded."))
					.put("redirect", Imperator.getUrlBuilder().buildLink(""));
			return output;
		}
		output.put("state", game.getState().ordinal());
		if(game.getPlayers().contains(member)) {
			output.put("messages", ChatUpdate.getMessages(gid, time));
		}
		if(game.getTime() > time && game.getState() != Game.State.FINISHED) {
			fillOutput(time, game, member, output);
		}
		return output;
	}

	protected void fillOutput(long time, Game game, Member member, JSONObject output) {
		if(!game.hasStarted()) {
			throw new IllegalStateException("Game has not yet started");
		}
		if(time == 0) {
			addRegions(game, output);
		}
		addTerritories(time, game, member, output);
		addPlayers(game, output);
		output.put("conquered", game.hasConquered())
				.put("turn", game.getCurrentPlayer().getId())
				.put("units", game.getUnits())
				.put("attacks", getAttacks(game));
		if(game.getPlayers().contains(member)) {
			Player player = game.getPlayerById(member.getId());
			output.put("autoroll", player.getAutoRoll())
					.put("cards", new JSONObject()
							.put(String.valueOf(Cards.Card.ARTILLERY), player.getCards().getArtillery())
							.put(String.valueOf(Cards.Card.INFANTRY), player.getCards().getInfantry())
							.put(String.valueOf(Cards.Card.CAVALRY), player.getCards().getCavalry())
							.put(String.valueOf(Cards.Card.JOKER), player.getCards().getJokers()))
					.put("mission", new JSONObject()
							.put("name", player.getMission().getName())
							.put("description", player.getMission().getDescription(member.getLanguage())));
		}
		JSONArray combatLog = new JSONArray();
		output.put("combatlog", combatLog);
		DateFormat format = new SimpleDateFormat(Api.DATE_ATOM, Locale.US);
		for(LogEntry entry : Imperator.getData().getCombatLogs(game, time)) {
			combatLog.put(new JSONObject()
					.put("type", entry.getType().ordinal())
					.put("time", format.format(new Date(entry.getTime())))
					.put("message", entry.getMessage()));
		}
	}

	private void addRegions(Game game, JSONObject output) {
		JSONObject regions = new JSONObject();
		output.put("regions", regions);
		for(Region region : game.getMap().getRegions().values()) {
			JSONArray territories = new JSONArray();
			for(Territory territory : region.getTerritories()) {
				territories.put(territory.getId());
			}
			regions.put(region.getId(), new JSONObject()
					.put("id", region.getId())
					.put("territories", territories)
					.put("units", region.getUnits()));
		}
	}

	private void addTerritories(long time, Game game, Member member, JSONObject output) {
		JSONObject territories = new JSONObject();
		output.put("territories", territories);
		for(Territory territory : game.getMap().getTerritories().values()) {
			JSONObject json = new JSONObject()
					.put("units", territory.getUnits())
					.put("uid", territory.getOwner().getId());
			territories.put(territory.getId(), json);
			if(time == 0) {
				JSONArray borders = new JSONArray();
				for(Territory border : territory.getBorders()) {
					borders.put(border.getId());
				}
				json.put("id", territory.getId())
						.put("name", member.getLanguage().translate(territory.getName()))
						.put("borders", borders);
			}
		}
	}

	private void addPlayers(Game game, JSONObject output) {
		JSONObject players = new JSONObject();
		output.put("players", players);
		for(Player player : game.getPlayers()) {
			players.put(String.valueOf(player.getId()), new JSONObject()
					.put("color", player.getColor())
					.put("id", player.getId())
					.put("name", player.getName())
					.put("playing", player.getState() != Player.State.GAME_OVER));
		}
	}

	static boolean playerInGame(User user, int gid) {
		Game game = Imperator.getData().getGame(gid);
		if(game != null) {
			return game.getPlayers().contains(user);
		}
		return false;
	}

	static JSONArray getAttacks(Game game) {
		JSONArray attacks = new JSONArray();
		for(Attack attack : game.getAttacks()) {
			attacks.put(getAttackJSON(attack));
		}
		return attacks;
	}

	static JSONObject getAttackJSON(Attack attack) {
		JSONObject out = new JSONObject()
				.put("attack", attack.getAttacker().getId())
				.put("defender", attack.getDefender().getId())
				.put("attackroll", attack.getAttackRoll())
				.put("move", attack.getMove());
		if(attack.getDefendRoll() != null) {
			out.put("defendroll", attack.getDefendRoll());
		}
		return out;
	}
}