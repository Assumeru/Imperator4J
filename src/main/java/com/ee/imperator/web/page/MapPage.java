package com.ee.imperator.web.page;

import java.util.Arrays;

import org.ee.web.exception.NotFoundException;

import com.ee.imperator.map.Map;
import com.ee.imperator.web.PathParam;
import com.ee.imperator.web.context.PageContext;

public class MapPage extends AbstractVariablePage {
	public MapPage() {
		super("map/{id : [-]{0,1}[0-9]+}/{name : .*}", "map", "map");
	}

	public void setVariables(PageContext context, @PathParam("id") int id) {
		Map map = context.getMapProvider().getMap(id);
		if(map == null) {
			throw new NotFoundException();
		} else {
			context.setVariable(PageContext.VARIABLE_TITLE, map.getName());
			context.setVariable(PageContext.VARIABLE_CSS, Arrays.asList("map.css"));
			PageContext.VARIABLE_JAVASCRIPT.add(context, "map.js");
			context.setVariable("map", map);
		}
	}
}
