package com.ee.imperator.url;

import com.ee.imperator.game.Game;
import com.ee.imperator.map.HasFlag;
import com.ee.imperator.map.Map;

public interface UrlBuilder {
	String buildLink(String url);

	String css(String file);

	String javascript(String file);

	String image(String file);

	String flag(HasFlag location);

	String map(Map map);

	String game(Game game);
}
