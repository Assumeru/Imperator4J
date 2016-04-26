package com.ee.imperator.request.page;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.ConfigurationException;
import com.ee.imperator.exception.FormException;
import com.ee.imperator.game.Game;
import com.ee.imperator.request.PageContext;
import com.ee.imperator.request.page.form.JoinGameForm;
import com.ee.imperator.user.Player;

public class GamePage extends AbstractVariablePage {
	private static final Logger LOG = LogManager.createLogger();

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
			// TODO post game
		} else if(game.hasStarted()) {
			// TODO in game
		} else {
			setPreGameVariables(context, game);
		}
	}

	private void setPreGameVariables(PageContext context, Game game) {
		if(!game.getPlayers().contains(context.getUser())) {
			Map<String, String> colors = getColors(game);
			if(context.getPostParams() != null) {
				joinGame(context, game, colors);
			}
			context.setVariable("colors", colors);
		} else if(context.getPostParams() != null) {
			//TODO controls
		}
		context.setVariable(PageContext.VARIABLE_BODY, "pregame::fragment");
		context.setVariable("canKick", game.getOwner().equals(context.getUser()));
		context.setVariable(PageContext.VARIABLE_CSS, Arrays.asList("newgame.css"));
		context.setVariable("code", context.getGetParams().getFirst("code"));
	}

	private void joinGame(PageContext context, Game game, Map<String, String> colors) {
		try {
			JoinGameForm form = new JoinGameForm(context, game, colors);
			Player player = new Player(context.getUser());
			player.setColor(form.getColor());
			if(Imperator.getData().addPlayerToGame(player, game)) {
				throw new WebApplicationException(Response.seeOther(new URI(context.game(game))).build());
			}
		} catch (FormException e) {
			if(e.getName() != null) {
				context.setVariable(e.getName(), e.getMessage());
			}
			LOG.v(e);
		} catch (URISyntaxException e) {
			LOG.e(e);
			throw new RuntimeException(e);
		}
	}

	private Map<String, String> getColors(Game game) {
		String[] keys = Imperator.getConfig().getStrings(Player.class, "color.names");
		String[] values = Imperator.getConfig().getStrings(Player.class, "color.hex");
		if(keys.length != values.length) {
			throw new ConfigurationException("color.names.length != color.hex.length");
		}
		Set<String> inUse = new HashSet<>();
		for(Player player : game.getPlayers()) {
			inUse.add(player.getColor());
		}
		Map<String, String> colors = new LinkedHashMap<>();
		for(int i = 0; i < keys.length; i++) {
			if(!inUse.contains(values[i])) {
				colors.put(keys[i], values[i]);
			}
		}
		return colors;
	}
}
