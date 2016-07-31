package com.ee.imperator.web.page.form;

import java.util.Arrays;

import com.ee.imperator.exception.FormException;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Map;
import com.ee.imperator.user.Player;
import com.ee.imperator.web.context.PageContext;

public class NewGameForm extends Form {
	private String name;
	private Map map;
	private String color;
	private String password;

	public NewGameForm(PageContext context) throws FormException {
		super(context);
		setName();
		setMap();
		setColor();
		setPassword();
	}

	private void setName() throws FormException {
		name = getPostString("name");
		if(name == null) {
			name = "";
			throw new FormException("Please enter a name");
		}
		name = name.trim();
		if(name.isEmpty()) {
			throw new FormException("Please enter a name");
		} else if(name.length() > context.getConfig().getInt(Game.class, "name.maxLength")) {
			throw new FormException("Please enter a shorter name");
		}
	}

	private void setMap() throws FormException {
		map = context.getMapProvider().getMap(getPostInt("map"));
		if(map == null) {
			throw new FormException("Map does not exist");
		}
	}

	private void setColor() throws FormException {
		color = getPostString("color");
		if(!Arrays.asList(context.getConfig().getStrings(Player.class, "color.hex")).contains(color)) {
			throw new FormException("Illegal color");
		}
	}

	private void setPassword() {
		password = getPostString("password");
		if(password != null && password.isEmpty()) {
			password = null;
		}
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
