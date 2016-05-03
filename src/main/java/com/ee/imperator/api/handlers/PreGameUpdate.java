package com.ee.imperator.api.handlers;

import org.json.JSONObject;

import com.ee.imperator.Imperator;
import com.ee.imperator.game.Game;
import com.ee.imperator.url.UrlBuilder;
import com.ee.imperator.user.Member;

@Request(mode = "update", type = "pregame")
public class PreGameUpdate extends GameUpdate {
	@Override
	protected void fillOutput(long time, Game game, Member member, JSONObject output) {
		if(!game.getPlayers().contains(member)) {
			output.put("gameState", "You have been kicked from this game.").put("redirect", Imperator.getUrlBuilder().buildLink(""));
		} else if(game.hasStarted()) {
			UrlBuilder url = Imperator.getUrlBuilder();
			output.put("gameState", "This game has started.").put("redirect", url.buildLink(url.game(game)));
		} else {
			//TODO
		}
	}
}
