package com.ee.imperator.web.page;

import com.ee.imperator.web.NavigationPage;
import com.ee.imperator.web.context.PageContext;

@NavigationPage(index = 3, name = "Rankings")
public class Rankings extends ImperatorPage {
	public Rankings() {
		super("rankings", "rankings", "Rankings");
	}

	@Override
	protected void setVariables(PageContext context) {
		context.setVariable("users", context.getState().getMembers());
		PageContext.VARIABLE_JAVASCRIPT.addAll(context, "lib/jquery.tablesorter.min.js", "tablesorter.js");
	}
}
