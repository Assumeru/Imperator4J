package com.ee.imperator.request.page;

import org.ee.web.request.page.NavigationPage;

import com.ee.imperator.Imperator;
import com.ee.imperator.request.PageContext;

@NavigationPage(index = 0, name = "Home")
public class Index extends ImperatorPage {
	public Index() {
		super("", null, "Home");
	}

	@Override
	protected void setVariables(PageContext context) {
		if(context.getUser().isLoggedIn()) {
			context.setVariable(PageContext.VARIABLE_BODY, "gamelist");
			context.setVariable("games", Imperator.getGames());
		} else {
			context.setVariable(PageContext.VARIABLE_BODY, "splash");
		}
	}
}
