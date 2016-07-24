package com.ee.imperator.request.page;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.ForbiddenException;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.request.page.NavigationPage;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.ConfigurationException;
import com.ee.imperator.exception.FormException;
import com.ee.imperator.exception.TransactionException;
import com.ee.imperator.game.Game;
import com.ee.imperator.request.context.PageContext;
import com.ee.imperator.request.page.form.NewGameForm;
import com.ee.imperator.user.Player;

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
		if(context.getPostParams() != null) {
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
		context.setVariable("maps", Imperator.getMapProvider().getMaps());
		context.setVariable("colors", getColors());
		context.setVariable("name", context.getUser().getLanguage().translate("%1$s's game", context.getUser().getName()));
	}

	private void createNewGame(NewGameForm form, PageContext context) throws TransactionException {
		Player owner = new Player(context.getUser());
		owner.setColor(form.getColor());
		String password = form.getPassword();
		if(password != null) {
			password = Imperator.getHasher().hash(password);
		}
		Game game = Imperator.getState().createGame(owner, form.getMap(), form.getName(), password);
		redirect(Imperator.getUrlBuilder().game(game));
	}

	private Map<String, String> getColors() {
		String[] keys = Imperator.getConfig().getStrings(Player.class, "color.names");
		String[] values = Imperator.getConfig().getStrings(Player.class, "color.hex");
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
