package com.ee.imperator.request.thymeleaf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.ee.web.request.page.WebPage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.ee.imperator.request.page.PageContext;
import com.ee.imperator.user.Member;

public class ThymeleafContext implements PageContext {
	private final TemplateEngine templateEngine;
	private final WebContext context;
	private final Member user;
	private final List<WebPage> navigationPages;

	public ThymeleafContext(TemplateEngine templateEngine, WebContext context, Member user, List<WebPage> navigationPages) {
		this.templateEngine = templateEngine;
		this.context = context;
		this.user = user;
		this.navigationPages = navigationPages;
	}

	@Override
	public Member getUser() {
		return user;
	}

	@Override
	public ByteArrayOutputStream processPage() {
		final ByteArrayOutputStream response = new ByteArrayOutputStream();
		final Writer writer = new OutputStreamWriter(response);
		templateEngine.process("page", context, writer);
		try {
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return response;
	}

	@Override
	public void setVariable(String key, Object value) {
		context.setVariable(key, value);
	}

	@Override
	public List<WebPage> getNavigationPages() {
		return navigationPages;
	}

	@Override
	public String css(String file) {
		return "/css/" + file;
	}

	@Override
	public String javascript(String file) {
		return "/js/" + file;
	}

	@Override
	public String image(String file) {
		return "/img/" + file;
	}
}
