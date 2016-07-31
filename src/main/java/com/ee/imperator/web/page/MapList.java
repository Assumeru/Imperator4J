package com.ee.imperator.web.page;

import com.ee.imperator.web.NavigationPage;
import com.ee.imperator.web.context.PageContext;

@NavigationPage(index = 4, name = "Maps")
public class MapList extends ImperatorPage {
	public MapList() {
		super("maps", "maps", "Maps");
	}

	@Override
	protected void setVariables(PageContext context) {
		PageContext.VARIABLE_JAVASCRIPT.addAll(context, "lib/jquery.tablesorter.min.js", "tablesorter.js");
		context.setVariable("maps", context.getMapProvider().getMaps());
	}
}
