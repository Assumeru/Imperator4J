package com.ee.imperator.web.page.form;

import java.util.Arrays;

import com.ee.imperator.exception.FormException;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Map;
import com.ee.imperator.user.Player;
import com.ee.imperator.web.context.PageContext;

public class NewGameForm extends Form {
	private final int maxNameLength;
	private final String name;
	private final Map map;
	private final String color;
	private final String password;

	public NewGameForm(PageContext context) throws FormException {
		super(context);
		maxNameLength = context.getIntSetting(Game.class, "name.maxLength");
		name = setName();
		map = setMap();
		color = setColor();
		password = setPassword();
	}

	private String setName() throws FormException {
		String name = getPostString("name");
		if(name == null) {
			name = "";
			throw new FormException("Please enter a name");
		}
		name = name.trim();
		if(name.isEmpty()) {
			throw new FormException("Please enter a name");
		} else if(name.length() > maxNameLength) {
			throw new FormException("Please enter a shorter name");
		}
		return name;
	}

	private Map setMap() throws FormException {
		Map map = context.getMapProvider().getMap(getPostInt("map"));
		if(map == null) {
			throw new FormException("Map does not exist");
		}
		return map;
	}

	private String setColor() throws FormException {
		String color = getPostString("color");
		if(!Arrays.asList(context.getStringsSetting(Player.class, "color.hex")).contains(color)) {
			throw new FormException("Illegal color");
		}
		return color;
	}

	private String setPassword() {
		String password = getPostString("password");
		if(password != null && password.isEmpty()) {
			password = null;
		}
		return password;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public Map getMap() {
		return map;
	}

	public String getPassword() {
		return password;
	}
}
