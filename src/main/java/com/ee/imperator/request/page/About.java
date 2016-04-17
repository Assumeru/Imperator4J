package com.ee.imperator.request.page;

import org.ee.web.request.page.NavigationPage;

import com.ee.imperator.request.PageContext;

@NavigationPage(index = 5, name = "About")
public class About extends ImperatorPage {
	public About() {
		super("about", "about", "About");
	}

	@Override
	protected void setVariables(PageContext context) {
	}
}
