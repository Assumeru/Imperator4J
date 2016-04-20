package com.ee.imperator.request.page;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.ee.web.request.page.NavigationPage;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.ConfigurationException;
import com.ee.imperator.request.PageContext;
import com.ee.imperator.user.Player;

@NavigationPage(index = 2, name = "New Game")
public class NewGamePage extends ImperatorPage {
	public NewGamePage() {
		super("game/new", "newgame", "New Game");
	}

	@Override
	protected void setVariables(PageContext context) {
		if(!context.getUser().isLoggedIn()) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		context.setVariable(PageContext.VARIABLE_CSS, Arrays.asList("newgame.css"));
		context.setVariable("maps", Imperator.getData().getMaps());
		context.setVariable("colors", getColors());
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
