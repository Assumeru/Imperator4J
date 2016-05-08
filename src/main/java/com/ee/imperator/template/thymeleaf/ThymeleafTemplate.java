package com.ee.imperator.template.thymeleaf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.AbstractContext;

import com.ee.imperator.template.Template;

public class ThymeleafTemplate implements Template {
	private final String template;
	private final TemplateEngine engine;
	private final AbstractContext context;

	public ThymeleafTemplate(String template, TemplateEngine engine, AbstractContext context) {
		this.template = template;
		this.engine = engine;
		this.context = context;
	}

	@Override
	public Template setVariable(String key, Object value) {
		context.setVariable(key, value);
		return this;
	}

	@Override
	public String process() {
		return engine.process(template, context);
	}

	@Override
	public void process(OutputStream output) throws IOException {
		final Writer writer = new OutputStreamWriter(output);
		engine.process(template, context, writer);
		writer.flush();
	}
}
