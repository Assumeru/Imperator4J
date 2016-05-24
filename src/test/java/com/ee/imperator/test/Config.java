package com.ee.imperator.test;

import java.util.HashMap;
import java.util.Map;

import org.ee.config.AbstractConfig;

import com.ee.imperator.data.ChatState;
import com.ee.imperator.data.GameState;
import com.ee.imperator.data.MemberState;
import com.ee.imperator.i18n.ClientSideLanguageProvider;
import com.ee.imperator.map.MapProvider;
import com.ee.imperator.template.TemplateProvider;
import com.ee.imperator.test.i18n.DummyClientSideLanguageProvider;
import com.ee.imperator.test.map.MemoryMapProvider;
import com.ee.imperator.test.state.DummyChatState;
import com.ee.imperator.test.state.MemoryGameState;
import com.ee.imperator.test.state.MemoryMemberState;
import com.ee.imperator.test.template.DummyTemplateProvider;
import com.ee.imperator.test.url.DummyUrlBuilder;
import com.ee.imperator.url.UrlBuilder;

public class Config extends AbstractConfig {
	private Map<String, String> values;

	public Config() {
		values = new HashMap<>();
		values.put(GameState.class.getName(), MemoryGameState.class.getName());
		values.put(MemberState.class.getName(), MemoryMemberState.class.getName());
		values.put(ChatState.class.getName(), DummyChatState.class.getName());
		values.put(UrlBuilder.class.getName(), DummyUrlBuilder.class.getName());
		values.put(ClientSideLanguageProvider.class.getName(), DummyClientSideLanguageProvider.class.getName());
		values.put(TemplateProvider.class.getName(), DummyTemplateProvider.class.getName());
		values.put(MapProvider.class.getName(), MemoryMapProvider.class.getName());
	}

	@Override
	public String getString(String key) {
		return values.get(key);
	}
}
