package com.ee.imperator.web.page;

import com.ee.imperator.web.ImperatorRequestHandler;
import com.ee.imperator.web.NavigationPage;
import com.ee.imperator.web.context.PageContext;

@NavigationPage(index = 5, name = "About")
public class About extends ImperatorPage {
	public About(ImperatorRequestHandler handler) {
		super(handler, "about", "about", "About");
	}

	@Override
	protected void setVariables(PageContext context) {
	}
}
