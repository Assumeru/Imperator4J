package com.ee.imperator.request.context;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;

import org.ee.config.Config;
import org.ee.i18n.Language;
import org.ee.web.request.page.WebPage;

import com.ee.imperator.Imperator;
import com.ee.imperator.url.UrlBuilder;
import com.ee.imperator.user.Member;

public interface PageContext {
	public static final String VARIABLE_BODY = "body";
	public static final String VARIABLE_TITLE = "title";
	public static final MapVariable VARIABLE_JAVASCRIPT_SETTINGS = new MapVariable("javascriptSettings");
	public static final Variable<Language> VARIABLE_LANGUAGE = new Variable<>("i18n", ctx -> ctx.getUser().getLanguage());
	public static final Variable<List<WebPage>> VARIABLE_NAVIGATION = new Variable<>("navPages", ctx -> ctx.getNavigationPages());
	public static final Variable<Boolean> VARIABLE_SHOW_FOOTER = new Variable<>("showFooter", ctx -> true);
	public static final Variable<Integer> VARIABLE_YEAR = new Variable<>("date", ctx -> Calendar.getInstance().get(Calendar.YEAR));
	public static final Variable<List<String>> VARIABLE_CSS = new Variable<>("css", ctx -> Collections.emptyList());
	public static final Variable<List<String>> VARIABLE_JAVASCRIPT = new Variable<>("javascript", ctx -> Collections.emptyList());
	public static final Variable<PageContext> VARIABLE_CONTEXT = new Variable<>("ctx", ctx -> ctx);
	public static final Variable<String> VARIABLE_MAIN_CLASS = new Variable<>("mainClass", ctx -> "container");
	public static final Variable<Config> VARIABLE_CONFIG = new Variable<>("cfg", ctx -> Imperator.getConfig());
	public static final Variable<UrlBuilder> VARIABLE_URL_BUILDER = new Variable<>("url", ctx -> Imperator.getUrlBuilder());
	public static final Variable<?>[] DEFAULT_VARIABLES = { VARIABLE_CONFIG, VARIABLE_LANGUAGE, VARIABLE_NAVIGATION, VARIABLE_SHOW_FOOTER, VARIABLE_YEAR, VARIABLE_CSS, VARIABLE_JAVASCRIPT, VARIABLE_CONTEXT, VARIABLE_MAIN_CLASS, VARIABLE_URL_BUILDER };

	Member getUser();

	java.util.Map<String, Cookie> getCookies();

	MultivaluedMap<String, String> getGetParams();

	MultivaluedMap<String, String> getPostParams();

	ByteArrayOutputStream processPage();

	void setVariable(String key, Object value);

	List<WebPage> getNavigationPages();

	String getPath();

	default void setVariable(Variable<?> variable) {
		setVariable(variable, variable.getDefaultValue(this));
	}

	default void setVariable(Variable<?> variable, Object value) {
		variable.setOn(this, value);
	}
}
