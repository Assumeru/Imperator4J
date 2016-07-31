package com.ee.imperator.web.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.ee.web.request.Request;
import org.ee.web.request.filter.RequestFilter;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.ImperatorContext;
import com.ee.imperator.exception.PageException;
import com.ee.imperator.template.Template;
import com.ee.imperator.user.Member;

public class DefaultPageContext extends ImperatorContext implements PageContext {
	private final Template template;
	private final Member user;
	private final List<RequestFilter> navigationPages;
	private final Request request;

	public DefaultPageContext(ImperatorApplicationContext context, List<RequestFilter> navigationPages, Request request) {
		super(context.getApplication());
		this.template = context.getTemplateProvider().createTemplate("page", request.getContext().getServletRequest(), request.getContext().getServletResponse(), request.getContext().getContext());
		this.user = context.getState().getMember(request);
		this.navigationPages = navigationPages;
		this.request = request;
	}

	@Override
	public Member getUser() {
		return user;
	}

	@Override
	public List<RequestFilter> getNavigationPages() {
		return navigationPages;
	}

	@Override
	public ByteArrayOutputStream processPage() {
		final ByteArrayOutputStream response = new ByteArrayOutputStream();
		try {
			template.process(response);
		} catch (IOException e) {
			throw new PageException(e);
		}
		return response;
	}

	@Override
	public void setVariable(String key, Object value) {
		template.setVariable(key, value);
	}

	@Override
	public Request getRequest() {
		return request;
	}
}
