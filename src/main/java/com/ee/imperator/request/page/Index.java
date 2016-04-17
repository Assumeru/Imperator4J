package com.ee.imperator.request.page;

import org.ee.web.request.page.NavigationPage;

@NavigationPage(index = 0, name = "Home")
public class Index extends ImperatorPage {
	public Index() {
		super("", null, "Home");
	}

	@Override
	protected void setVariables(PageContext context) {
		if(context.getUser().isLoggedIn()) {
			
		} else {
			context.setVariable(PageContext.VARIABLE_BODY, "splash");
		}
	}
}
