package com.ee.imperator.web.page.form;

import com.ee.imperator.exception.FormException;
import com.ee.imperator.web.context.PageContext;

public class Form {
	protected final PageContext context;

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
		return context.getRequest().getPostParameters().getFirst(key);
	}
}
