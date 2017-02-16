package com.ee.imperator.web.page;

import org.ee.web.exception.ForbiddenException;

import com.ee.imperator.web.ImperatorRequestHandler;
import com.ee.imperator.web.NavigationPage;
import com.ee.imperator.web.context.PageContext;

@NavigationPage(index = 1, name = "My Games")
public class MyGames extends ImperatorPage {
	public MyGames(ImperatorRequestHandler handler) {
		super(handler, "my-games", "gamelist", "My Games");
	}

	@Override
	protected void setVariables(PageContext context) {
		if(context.getUser().isGuest()) {
			throw new ForbiddenException();
		}
		addChatJavascript(context, 0, context.getUser().canDeleteMessages());
		PageContext.VARIABLE_JAVASCRIPT.add(context, "gamelist-filter.js");
		context.setVariable("games", context.getState().getGames(context.getUser()));
	}
}
