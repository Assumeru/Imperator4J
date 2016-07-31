package com.ee.imperator.web.page;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ee.config.Config;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.exception.ForbiddenException;
import org.ee.web.request.Request.Method;

import com.ee.imperator.exception.ConfigurationException;
import com.ee.imperator.exception.FormException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.user.Player;
import com.ee.imperator.web.NavigationPage;
import com.ee.imperator.web.context.PageContext;
import com.ee.imperator.web.page.form.NewGameForm;

@NavigationPage(index = 2, name = "New Game")
public class NewGamePage extends ImperatorPage {
	private static final Logger LOG = LogManager.createLogger();

	public NewGamePage() {
		super("game/new", "newgame", "New Game");
	}

	@Override
	protected void setVariables(PageContext context) {
		if(context.getUser().isGuest()) {
			throw new ForbiddenException();
		}
		if(context.getRequest().getMethod() == Method.POST) {
			try {
				NewGameForm form = new NewGameForm(context);
				createNewGame(form, context);
				return;
			} catch (FormException e) {
				context.setVariable("error", e.getMessage());
				LOG.v(e);
			} catch(TransactionException e) {
				LOG.e(e);
			}
		}
		context.setVariable(PageContext.VARIABLE_CSS, Arrays.asList("newgame.css"));
		context.setVariable("maps", context.getMapProvider().getMaps());
		context.setVariable("colors", getColors(context.getConfig()));
		context.setVariable("name", context.getUser().getLanguage().translate("%1$s's game", context.getUser().getName()));
	}

	private void createNewGame(NewGameForm form, PageContext context) throws TransactionException {
		Player owner = new Player(context.getUser());
		owner.setColor(form.getColor());
		String password = form.getPassword();
		if(password != null) {
			password = context.getHasher().hash(password);
		}
		Game game = context.getState().createGame(owner, form.getMap(), form.getName(), password);
		redirect(context.getUrlBuilder().game(game));
	}

	private Map<String, String> getColors(Config config) {
		String[] keys = config.getStrings(Player.class, "color.names");
		String[] values = config.getStrings(Player.class, "color.hex");
		if(keys.length != values.length) {
			throw new ConfigurationException("color.names.length != color.hex.length");
		}
		Map<String, String> colors = new LinkedHashMap<>();
		for(int i = 0; i < keys.length; i++) {
			colors.put(keys[i], values[i]);
		}
		return colors;
	}
}
