package com.ee.imperator.request.page;

import java.util.Arrays;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.ee.web.request.page.NavigationPage;

import com.ee.imperator.request.PageContext;

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
	}
}
