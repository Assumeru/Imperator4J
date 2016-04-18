package com.ee.imperator.request.page;

import java.util.Arrays;

import org.ee.web.request.page.NavigationPage;

import com.ee.imperator.Imperator;
import com.ee.imperator.request.PageContext;

@NavigationPage(index = 4, name = "Maps")
public class MapList extends ImperatorPage {
	public MapList() {
		super("maps", "maps", "Maps");
	}

	@Override
	protected void setVariables(PageContext context) {
		context.setVariable(PageContext.VARIABLE_JAVASCRIPT, Arrays.asList("jquery.tablesorter.min.js", "tablesorter.js"));
		context.setVariable("maps", Imperator.getData().getMaps());
	}
}
