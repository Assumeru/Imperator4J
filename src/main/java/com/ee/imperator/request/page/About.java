package com.ee.imperator.request.page;

import org.ee.web.request.page.NavigationPage;

@NavigationPage(index = 5, name = "About")
public class About extends AbstractPage {
	public About() {
		super("about", "about", "About");
	}

	@Override
	protected void setVariables(PageContext context) {
	}
}
