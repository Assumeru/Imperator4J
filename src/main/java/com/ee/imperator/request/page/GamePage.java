package com.ee.imperator.request.page;

import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.ee.imperator.Imperator;
import com.ee.imperator.game.Game;
import com.ee.imperator.request.PageContext;

public class GamePage extends AbstractVariablePage {
	public GamePage() {
		super("game/{id : [-]{0,1}[0-9]+}/{name : .*}", "game", null);
	}

	public void setVariables(PageContext context, @PathParam("id") int id) {
		if(!context.getUser().isLoggedIn()) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		Game game = Imperator.getData().getGame(id);
		if(game == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		context.setVariable(PageContext.VARIABLE_TITLE, game.getName());
		context.setVariable("game", game);
		if(game.hasEnded()) {
			//TODO post game
		} else if(game.hasStarted()) {
			//TODO in game
		} else {
			context.setVariable(PageContext.VARIABLE_BODY, "pregame::fragment");
			context.setVariable("canKick", game.getOwner().equals(context.getUser()));
		}
	}
}
