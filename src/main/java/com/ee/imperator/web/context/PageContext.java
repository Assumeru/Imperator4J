package com.ee.imperator.web.context;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.ee.i18n.Language;
import org.ee.web.request.Request;
import org.ee.web.request.filter.RequestFilter;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.url.UrlBuilder;
import com.ee.imperator.user.Member;

public interface PageContext extends ImperatorApplicationContext {
	public static final String VARIABLE_BODY = "body";
	public static final String VARIABLE_TITLE = "title";
	public static final MapVariable<String, Object> VARIABLE_JAVASCRIPT_SETTINGS = new MapVariable<>("javascriptSettings");
	public static final Variable<Language> VARIABLE_LANGUAGE = new Variable<>("i18n", ctx -> ctx.getUser().getLanguage());
	public static final Variable<List<RequestFilter>> VARIABLE_NAVIGATION = new Variable<>("navPages", ctx -> ctx.getNavigationPages());
	public static final Variable<Boolean> VARIABLE_SHOW_FOOTER = new Variable<>("showFooter", ctx -> true);
	public static final Variable<Integer> VARIABLE_YEAR = new Variable<>("date", ctx -> Calendar.getInstance().get(Calendar.YEAR));
	public static final Variable<List<String>> VARIABLE_CSS = new Variable<>("css", ctx -> Collections.emptyList());
	public static final ListVariable<String> VARIABLE_JAVASCRIPT = new ListVariable<>("javascript");
	public static final Variable<PageContext> VARIABLE_CONTEXT = new Variable<>("ctx", ctx -> ctx);
	public static final Variable<String> VARIABLE_MAIN_CLASS = new Variable<>("mainClass", ctx -> "container");
	public static final Variable<UrlBuilder> VARIABLE_URL_BUILDER = new Variable<>("url", ctx -> ctx.getUrlBuilder());
	public static final List<Variable<?>> DEFAULT_VARIABLES = Arrays.asList(VARIABLE_LANGUAGE, VARIABLE_NAVIGATION, VARIABLE_SHOW_FOOTER, VARIABLE_YEAR, VARIABLE_CSS, VARIABLE_JAVASCRIPT, VARIABLE_CONTEXT, VARIABLE_MAIN_CLASS, VARIABLE_URL_BUILDER);

	Member getUser();

	Request getRequest();

	ByteArrayOutputStream processPage();

	void setVariable(String key, Object value);

	List<RequestFilter> getNavigationPages();

	default void setVariable(Variable<?> variable) {
		setVariable(variable, variable.getDefaultValue(this));
	}

	default void setVariable(Variable<?> variable, Object value) {
		variable.setOn(this, value);
	}
}
