package com.ee.imperator.api.handlers;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Region;
import com.ee.imperator.map.Territory;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

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
		//TODO chat
		output.put("state", game.getState().ordinal());
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
		.put("units", game.getUnits());
		//TODO attacks+
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
}
