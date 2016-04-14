package com.ee.imperator.request.page;

import org.ee.web.request.page.NavigationPage;

@NavigationPage(index = 0, name = "Test")
public class Test extends AbstractPage {
	public Test() {
		super("test", "test", "Test page");
	}

	@Override
	protected void setVariables(PageContext context) {
	}
}
