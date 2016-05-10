package com.ee.imperator.request.page;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.ConfigurationException;
import com.ee.imperator.exception.FormException;
import com.ee.imperator.game.Game;
import com.ee.imperator.request.context.PageContext;
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
			setInGameVariables(context, game);
		} else {
			setPreGameVariables(context, game);
		}
	}

	private void setInGameVariables(PageContext context, Game game) {
		context.setVariable(PageContext.VARIABLE_BODY, "ingame::fragment");
		context.setVariable(PageContext.VARIABLE_CSS, Arrays.asList("game.css"));
		context.setVariable(PageContext.VARIABLE_SHOW_FOOTER, false);
		if(game.getPlayers().contains(context.getUser())) {
			context.setVariable(PageContext.VARIABLE_MAIN_CLASS, "container-fluid");
		} else {
			context.setVariable(PageContext.VARIABLE_MAIN_CLASS, "container-fluid not-player");
		}
		context.setVariable("uid", context.getUser().getId());
		context.setVariable("player", game.getPlayerById(context.getUser().getId()));
		addChatJavascript(context, game.getId(), context.getUser().canDeleteMessages() || game.getOwner().equals(context.getUser()));
		PageContext.VARIABLE_JAVASCRIPT.addAll(context, "classes.js", "map.js", "game.js");
	}

	private void setPreGameVariables(PageContext context, Game game) {
		if(!game.getPlayers().contains(context.getUser())) {
			context.setVariable(PageContext.VARIABLE_CSS, Arrays.asList("newgame.css"));
			Map<String, String> colors = getColors(game);
			if(context.getPostParams() != null && game.getPlayers().size() < game.getMap().getPlayers()) {
				joinGame(context, game, colors);
			}
			context.setVariable("colors", colors);
		} else {
			PageContext.VARIABLE_JAVASCRIPT.add(context, "pregame.js");
			addChatJavascript(context, game.getId(), context.getUser().canDeleteMessages() || game.getOwner().equals(context.getUser()));
			if(context.getPostParams() != null) {
				if(context.getUser().equals(game.getOwner())) {
					if(context.getPostParams().getFirst("startgame") != null && game.getPlayers().size() == game.getMap().getPlayers()) {
						startGame(context, game);
					} else if(context.getPostParams().getFirst("disband") != null) {
						deleteGame(game);
					}
				} else if(context.getPostParams().getFirst("leavegame") != null) {
					leaveGame(context, game);
				}
			}
		}
		context.setVariable(PageContext.VARIABLE_BODY, "pregame::fragment");
		context.setVariable("canKick", game.getOwner().equals(context.getUser()));
		context.setVariable("code", context.getGetParams().getFirst("code"));
	}

	private synchronized void joinGame(PageContext context, Game game, Map<String, String> colors) {
		if(game.getPlayers().size() < game.getMap().getPlayers()) {
			try {
				JoinGameForm form = new JoinGameForm(context, game, colors);
				Player player = new Player(context.getUser());
				player.setColor(form.getColor());
				if(Imperator.getData().addPlayerToGame(player, game)) {
					redirect(Imperator.getUrlBuilder().game(game));
				}
			} catch (FormException e) {
				if(e.getName() != null) {
					context.setVariable(e.getName(), e.getMessage());
				}
				LOG.v(e);
			}
		}
	}

	private void leaveGame(PageContext context, Game game) {
		if(Imperator.getData().removePlayerFromGame(game.getPlayerById(context.getUser().getId()), game)) {
			redirect(Imperator.getUrlBuilder().game(game));
		}
	}

	private void deleteGame(Game game) {
		if(Imperator.getData().deleteGame(game)) {
			redirect("/");
		}
	}

	private synchronized void startGame(PageContext context, Game game) {
		if(game.getPlayers().size() == game.getMap().getPlayers()) {
			Imperator.getData().startGame(game);
			redirect(Imperator.getUrlBuilder().game(game));
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
