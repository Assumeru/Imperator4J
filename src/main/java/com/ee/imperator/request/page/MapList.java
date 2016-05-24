package com.ee.imperator.request.page;

import org.ee.web.request.page.NavigationPage;

import com.ee.imperator.Imperator;
import com.ee.imperator.request.context.PageContext;

@NavigationPage(index = 4, name = "Maps")
public class MapList extends ImperatorPage {
	public MapList() {
		super("maps", "maps", "Maps");
	}

	@Override
	protected void setVariables(PageContext context) {
		PageContext.VARIABLE_JAVASCRIPT.addAll(context, "jquery.tablesorter.min.js", "tablesorter.js");
		context.setVariable("maps", Imperator.getMapProvider().getMaps());
	}
}
