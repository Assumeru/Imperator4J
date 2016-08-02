package com.ee.imperator.web.page;

import com.ee.imperator.web.NavigationPage;
import com.ee.imperator.web.context.PageContext;

@NavigationPage(index = 0, name = "Home")
public class Index extends ImperatorPage {
	public Index() {
		super("", null, "Home");
	}

	@Override
	protected void setVariables(PageContext context) {
		if(context.getUser().isGuest()) {
			context.setVariable(PageContext.VARIABLE_BODY, "splash::fragment");
		} else {
			addChatJavascript(context, 0, context.getUser().canDeleteMessages());
			PageContext.VARIABLE_JAVASCRIPT.add(context, "gamelist-filter.js");
			context.setVariable(PageContext.VARIABLE_BODY, "gamelist::fragment");
			context.setVariable("games", context.getState().getGames());
		}
	}
}
