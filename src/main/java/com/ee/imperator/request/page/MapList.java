package com.ee.imperator.request.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ee.web.request.page.NavigationPage;

import com.ee.imperator.Imperator;
import com.ee.imperator.map.Map;

@NavigationPage(index = 4, name = "Maps")
public class MapList extends AbstractPage {
	public MapList() {
		super("maps", "maps", "Maps");
	}

	@Override
	protected void setVariables(PageContext context) {
		context.setVariable(PageContext.VARIABLE_JAVASCRIPT, Arrays.asList("jquery.tablesorter.min.js", "tablesorter.js"));
		List<Map> maps = new ArrayList<>(Imperator.getMaps().values());
		maps.sort(null);
		context.setVariable("maps", maps);
	}
}
