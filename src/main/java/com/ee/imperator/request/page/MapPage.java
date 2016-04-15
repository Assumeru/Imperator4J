package com.ee.imperator.request.page;

import java.util.Arrays;

import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.ee.imperator.Imperator;
import com.ee.imperator.map.Map;

public class MapPage extends AbstractVariablePage {
	public MapPage() {
		super("map/{id : [-]{0,1}[0-9]+}/{name}", "map", "map");
	}

	public void setVariables(PageContext context, @PathParam("id") int id) {
		Map map = Imperator.getMaps().get(id);
		if(map == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		} else {
			context.setVariable(PageContext.VARIABLE_TITLE, map.getName());
			context.setVariable(PageContext.VARIABLE_CSS, Arrays.asList("map.css"));
			context.setVariable(PageContext.VARIABLE_JAVASCRIPT, Arrays.asList("map.js"));
			context.setVariable("map", map);
		}
	}
}
