package com.ee.imperator.request.page.form;

import com.ee.imperator.exception.FormException;
import com.ee.imperator.request.PageContext;

public class Form {
	private PageContext context;

	public Form(PageContext context) {
		this.context = context;
	}

	public int getPostInt(String key) throws FormException {
		String value = getPostString(key);
		if(value == null) {
			throw new FormException(key + " = " + null);
		}
		try {
			return Integer.parseInt(value);
		} catch(NumberFormatException e) {
			throw new FormException(key + " is not an int", e);
		}
	}

	public String getPostString(String key) {
		if(context.getPostParams() == null) {
			return null;
		}
		return context.getPostParams().getFirst(key);
	}
}
