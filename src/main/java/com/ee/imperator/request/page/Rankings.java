package com.ee.imperator.request.page;

import org.ee.web.request.page.NavigationPage;

import com.ee.imperator.Imperator;
import com.ee.imperator.request.context.PageContext;

@NavigationPage(index = 3, name = "Rankings")
public class Rankings extends ImperatorPage {
	public Rankings() {
		super("rankings", "rankings", "Rankings");
	}

	@Override
	protected void setVariables(PageContext context) {
		context.setVariable("users", Imperator.getState().getMembers());
		PageContext.VARIABLE_JAVASCRIPT.addAll(context, "lib/jquery.tablesorter.min.js", "tablesorter.js");
	}
}
