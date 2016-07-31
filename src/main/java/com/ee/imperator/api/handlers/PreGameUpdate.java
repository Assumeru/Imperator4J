package com.ee.imperator.api.handlers;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.game.Game;
import com.ee.imperator.url.UrlBuilder;
import com.ee.imperator.user.Member;
import com.ee.imperator.user.Player;

@Request(mode = "update", type = "pregame")
public class PreGameUpdate extends GameUpdate {
	public PreGameUpdate(ImperatorApplicationContext context) {
		super(context);
	}

	@Override
	protected void fillOutput(long time, Game game, Member member, JSONObject output) {
		if(!game.getPlayers().contains(member)) {
			output.put("gameState", member.getLanguage().translate("You have been kicked from this game."))
					.put("redirect", context.getUrlBuilder().buildLink(""));
		} else if(game.hasStarted()) {
			UrlBuilder url = context.getUrlBuilder();
			output.put("gameState", member.getLanguage().translate("This game has started."))
					.put("redirect", url.buildLink(url.game(game)));
		} else {
			JSONArray players = new JSONArray();
			output.put("maxPlayers", game.getMap().getPlayers())
					.put("owner", game.getOwner().getId())
					.put("players", players);
			for(Player player : game.getPlayers()) {
				players.put(new JSONObject()
						.put("name", player.getName())
						.put("id", player.getId())
						.put("color", player.getColor())
						.put("canKick", game.getOwner().equals(member) && !player.equals(member)));
			}
		}
	}
}
