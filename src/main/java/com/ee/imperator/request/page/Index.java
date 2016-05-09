package com.ee.imperator.request.page;

import org.ee.web.request.page.NavigationPage;

import com.ee.imperator.Imperator;
import com.ee.imperator.request.context.PageContext;

@NavigationPage(index = 0, name = "Home")
public class Index extends ImperatorPage {
	public Index() {
		super("", null, "Home");
	}

	@Override
	protected void setVariables(PageContext context) {
		if(context.getUser().isLoggedIn()) {
			addChatJavascript(context, 0, context.getUser().canDeleteMessages());
			PageContext.VARIABLE_JAVASCRIPT.add(context, "gamelist-filter.js");
			context.setVariable(PageContext.VARIABLE_BODY, "gamelist");
			context.setVariable("games", Imperator.getData().getGames());
		} else {
			context.setVariable(PageContext.VARIABLE_BODY, "splash");
		}
	}
}
