package com.ee.imperator.request.page;

import java.util.Arrays;
import java.util.Collections;

import org.ee.web.request.page.NavigationPage;

@NavigationPage(index = 4, name = "Maps")
public class MapList extends AbstractPage {
	public MapList() {
		super("maps", "maps", "Maps");
	}

	@Override
	protected void setVariables(PageContext context) {
		context.setVariable(PageContext.VARIABLE_JAVASCRIPT, Arrays.asList("jquery.tablesorter.min.js", "tablesorter.js"));
		context.setVariable("maps", Collections.EMPTY_LIST);
	}
}
