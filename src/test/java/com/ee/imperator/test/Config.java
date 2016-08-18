package com.ee.imperator.test;

import java.util.HashMap;
import java.util.Map;

import org.ee.config.AbstractConfig;
import org.ee.logger.LogProvider;
import org.ee.logger.system.SystemLogProvider;
import org.ee.web.request.DefaultRequestHandler;
import org.ee.web.request.RequestHandler;
import org.ee.web.response.DefaultResponseWriter;
import org.ee.web.response.ResponseWriter;

import com.ee.imperator.api.LongPolling;
import com.ee.imperator.crypt.csrf.CSRFTokenBuilder;
import com.ee.imperator.crypt.csrf.DefaultCSRFTokenBuilder;
import com.ee.imperator.data.ChatState;
import com.ee.imperator.data.GameState;
import com.ee.imperator.data.MemberState;
import com.ee.imperator.i18n.ClientSideLanguageProvider;
import com.ee.imperator.map.MapProvider;
import com.ee.imperator.task.CleanUp;
import com.ee.imperator.template.TemplateProvider;
import com.ee.imperator.test.i18n.DummyClientSideLanguageProvider;
import com.ee.imperator.test.map.MemoryMapProvider;
import com.ee.imperator.test.state.DummyChatState;
import com.ee.imperator.test.state.MemoryGameState;
import com.ee.imperator.test.state.MemoryMemberState;
import com.ee.imperator.test.template.DummyTemplateProvider;
import com.ee.imperator.test.url.DummyUrlBuilder;
import com.ee.imperator.url.UrlBuilder;
import com.ee.imperator.web.ImperatorRequestHandler;

public class Config extends AbstractConfig {
	private final Map<String, String> values;

	public Config() {
		values = new HashMap<>();
		put(GameState.class, MemoryGameState.class);
		put(MemberState.class, MemoryMemberState.class);
		put(ChatState.class, DummyChatState.class);
		put(UrlBuilder.class, DummyUrlBuilder.class);
		put(ClientSideLanguageProvider.class, DummyClientSideLanguageProvider.class);
		put(TemplateProvider.class, DummyTemplateProvider.class);
		put(MapProvider.class, MemoryMapProvider.class);
		put(RequestHandler.class, ImperatorRequestHandler.class);
		put(ResponseWriter.class, DefaultResponseWriter.class);
		put(LogProvider.class, SystemLogProvider.class);
		put(CSRFTokenBuilder.class, DefaultCSRFTokenBuilder.class);
		put(RequestHandler.class, DefaultRequestHandler.class);
		put(ResponseWriter.class, DefaultResponseWriter.class);
		put(DefaultCSRFTokenBuilder.class, "cookie", "_csrf");
		put(DefaultCSRFTokenBuilder.class, "maxCookieAge", 0);
		put(DefaultCSRFTokenBuilder.class, "header", "X-CSRF");
		put(LongPolling.class, "maxTries", 0);
		put(LongPolling.class, "sleep", 0);
		put(CleanUp.class, "inactiveGameTime", 1209600000);
		put(CleanUp.class, "maxChatMessageAge", 86400000);
		put(CleanUp.class, "maxFinishedGameAge", 86400000);
		put(CleanUp.class, "numberOfMessagesToKeep", 10);
		put(CleanUp.class, "sleep", 86400000);
	}

	private void put(Class<?> key, Class<?> value) {
		values.put(key.getName(), value.getName());
	}

	private void put(Class<?> type, String key, Object value) {
		values.put(org.ee.config.Config.getKey(type, key), String.valueOf(value));
	}

	@Override
	public String getString(String key) {
		return values.get(key);
	}
}
