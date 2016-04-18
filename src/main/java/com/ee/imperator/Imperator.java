package com.ee.imperator;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.ee.web.WebApplication;
import org.ee.web.request.AbstractRequestResolver;
import org.ee.web.request.Request;

import com.ee.imperator.data.DataProvider;
import com.ee.imperator.request.RequestResolver;
import com.ee.imperator.user.Member;

public class Imperator extends WebApplication {
	private static DataProvider dataProvider;

	public Imperator(@Context ServletContext context) {
		super(context);
	}

	@Override
	protected Class<? extends AbstractRequestResolver> getRequestResolver() {
		return RequestResolver.class;
	}

	public static Member getMember(Request request) {
		//TODO
		return dataProvider.getMember(request);
	}

	public static Member getMember(int id) {
		//TODO
		return dataProvider.getMember(id);
	}

	public static DataProvider getData() {
		return dataProvider;
	}
}
