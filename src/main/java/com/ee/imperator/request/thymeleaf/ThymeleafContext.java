package com.ee.imperator.request.thymeleaf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.ee.web.request.page.WebPage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.ee.imperator.request.AbstractPageContext;
import com.ee.imperator.user.Member;

public class ThymeleafContext extends AbstractPageContext {
	private final TemplateEngine templateEngine;
	private final WebContext context;

	public ThymeleafContext(TemplateEngine templateEngine, WebContext context, Member user, List<WebPage> navigationPages, String path) {
		super(user, navigationPages, path);
		this.templateEngine = templateEngine;
		this.context = context;
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
}
