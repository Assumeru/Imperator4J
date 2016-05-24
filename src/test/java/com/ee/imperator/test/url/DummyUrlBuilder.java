package com.ee.imperator.test.url;

import com.ee.imperator.game.Game;
import com.ee.imperator.map.HasFlag;
import com.ee.imperator.map.Map;
import com.ee.imperator.url.UrlBuilder;

public class DummyUrlBuilder implements UrlBuilder {
	@Override
	public String buildLink(String url) {
		return url;
	}

	@Override
	public String css(String file) {
		return file;
	}

	@Override
	public String javascript(String file) {
		return file;
	}

	@Override
	public String image(String file) {
		return file;
	}

	@Override
	public String flag(HasFlag location) {
		return location.getId();
	}

	@Override
	public String map(Map map) {
		return String.valueOf(map.getId());
	}

	@Override
	public String game(Game game) {
		return String.valueOf(game.getId());
	}
}
