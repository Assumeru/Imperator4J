package com.ee.imperator.web.page;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.ee.collection.ListMap;
import org.ee.config.ConfigurationException;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.exception.ForbiddenException;
import org.ee.web.exception.NotFoundException;
import org.ee.web.exception.WebException;
import org.ee.web.request.Request.Method;

import com.ee.imperator.exception.FormException;
import com.ee.imperator.exception.RequestException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Player;
import com.ee.imperator.web.ImperatorRequestHandler;
import com.ee.imperator.web.PathParam;
import com.ee.imperator.web.context.PageContext;
import com.ee.imperator.web.page.form.JoinGameForm;

public class GamePage extends AbstractVariablePage {
	private static final Logger LOG = LogManager.createLogger();

	public GamePage(ImperatorRequestHandler handler) {
		super(handler, "game/{id : [-]{0,1}[0-9]+}/{name : .*}", "game", null);
	}

	public void setVariables(PageContext context, @PathParam("id") int id) {
		if(context.getUser().isGuest()) {
			throw new ForbiddenException();
		}
		Game game = context.getState().getGame(id);
		if(game == null) {
			throw new NotFoundException();
		}
		context.setVariable(PageContext.VARIABLE_TITLE, game.getName());
		context.setVariable("game", game);
		if(game.hasEnded()) {
			setPostGameVariables(context, game);
		} else if(game.hasStarted()) {
			setInGameVariables(context, game);
		} else {
			setPreGameVariables(context, game);
		}
	}

	private void addChat(PageContext context, Game game) {
		addChatJavascript(context, game.getId(), context.getUser().canDeleteMessages() || game.getOwner().equals(context.getUser()));
	}

	private void setPostGameVariables(PageContext context, Game game) {
		context.setVariable(PageContext.VARIABLE_BODY, "postgame::fragment");
		PageContext.VARIABLE_JAVASCRIPT_SETTINGS.put(context, "postgame", true);
		if(game.getPlayers().contains(context.getUser())) {
			addChat(context, game);
		}
	}

	private void setInGameVariables(PageContext context, Game game) {
		context.setVariable(PageContext.VARIABLE_BODY, "ingame::fragment");
		context.setVariable(PageContext.VARIABLE_CSS, Arrays.asList("game.css"));
		context.setVariable(PageContext.VARIABLE_SHOW_FOOTER, false);
		if(game.getPlayers().contains(context.getUser())) {
			context.setVariable(PageContext.VARIABLE_MAIN_CLASS, "container-fluid");
			context.setVariable("player", game.getPlayerById(context.getUser().getId()));
			addChat(context, game);
		} else {
			addApiJavascript(context, game.getId());
			context.setVariable(PageContext.VARIABLE_MAIN_CLASS, "container-fluid not-player");
		}
		PageContext.VARIABLE_JAVASCRIPT_SETTINGS.put(context, "uid", context.getUser().getId());
		PageContext.VARIABLE_JAVASCRIPT.addAll(context, "classes.js", "map.js", "game.js");
	}

	private void setPreGameVariables(PageContext context, Game game) {
		if(!game.getPlayers().contains(context.getUser())) {
			context.setVariable(PageContext.VARIABLE_CSS, Arrays.asList("newgame.css"));
			Map<String, String> colors = getColors(context, game);
			if(context.getRequest().getMethod() == Method.POST && game.getPlayers().size() < game.getMap().getPlayers()) {
				joinGame(context, game, colors);
			}
			context.setVariable("colors", colors);
		} else {
			PageContext.VARIABLE_JAVASCRIPT.add(context, "pregame.js");
			addChat(context, game);
			if(context.getRequest().getMethod() == Method.POST) {
				ListMap<String, String> params = context.getRequest().getPostParameters();
				if(context.getUser().equals(game.getOwner())) {
					if(params.getFirst("startgame") != null && game.getPlayers().size() == game.getMap().getPlayers()) {
						startGame(context, game);
					} else if(params.getFirst("disband") != null) {
						deleteGame(context, game);
					}
				} else if(params.getFirst("leavegame") != null) {
					leaveGame(context, game);
				}
			}
		}
		context.setVariable(PageContext.VARIABLE_BODY, "pregame::fragment");
		context.setVariable("canKick", game.getOwner().equals(context.getUser()));
		context.setVariable("code", context.getRequest().getGetParameters().getFirst("code"));
	}

	private void joinGame(PageContext context, Game game, Map<String, String> colors) {
		if(game.getPlayers().size() < game.getMap().getPlayers()) {
			try {
				JoinGameForm form = new JoinGameForm(context, game, colors);
				Player player = new Player(context.getUser());
				player.setColor(form.getColor());
				context.getApi().getInternal().joinGame(game, player);
				redirect(context.getUrlBuilder().game(game));
			} catch (FormException e) {
				if(e.getName() != null) {
					context.setVariable(e.getName(), e.getMessage());
				}
				LOG.v(e);
			} catch (RequestException e) {
				LOG.e(e);
				redirect(context.getUrlBuilder().game(game));
			}
		}
	}

	private void leaveGame(PageContext context, Game game) {
		try {
			context.getApi().getInternal().leaveGame(game, game.getPlayerById(context.getUser().getId()));
		} catch (RequestException e) {
			LOG.e(e);
		}
		redirect(context.getUrlBuilder().game(game));
	}

	private void deleteGame(PageContext context, Game game) {
		try {
			context.getApi().getInternal().leaveGame(game, game.getOwner());
			redirect("/");
		} catch (RequestException e) {
			LOG.e(e);
		}
	}

	private void startGame(PageContext context, Game game) {
		try {
			context.getApi().getInternal().startGame(game);
		} catch (RequestException e) {
			throw new WebException(e);
		}
		redirect(context.getUrlBuilder().game(game));
	}

	private Map<String, String> getColors(PageContext context, Game game) {
		String[] keys = context.getStringsSetting(Player.class, "color.names");
		String[] values = context.getStringsSetting(Player.class, "color.hex");
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
