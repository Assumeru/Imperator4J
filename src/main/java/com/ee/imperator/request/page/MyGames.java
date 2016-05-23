package com.ee.imperator.request.page;

import javax.ws.rs.ForbiddenException;

import org.ee.web.request.page.NavigationPage;

import com.ee.imperator.Imperator;
import com.ee.imperator.request.context.PageContext;

@NavigationPage(index = 1, name = "My Games")
public class MyGames extends ImperatorPage {
	public MyGames() {
		super("my-games", "gamelist", "My Games");
	}

	@Override
	protected void setVariables(PageContext context) {
		if(!context.getUser().isLoggedIn()) {
			throw new ForbiddenException();
		}
		addChatJavascript(context, 0, context.getUser().canDeleteMessages());
		PageContext.VARIABLE_JAVASCRIPT.add(context, "gamelist-filter.js");
		context.setVariable("games", Imperator.getState().getGames(context.getUser()));
	}
}
