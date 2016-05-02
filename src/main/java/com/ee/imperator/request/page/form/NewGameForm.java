package com.ee.imperator.request.page.form;

import java.util.Arrays;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.FormException;
import com.ee.imperator.game.Game;
import com.ee.imperator.map.Map;
import com.ee.imperator.request.context.PageContext;
import com.ee.imperator.user.Player;

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
		} else if(name.length() > Imperator.getConfig().getInt(Game.class, "name.maxLength")) {
			throw new FormException("Please enter a shorter name");
		}
	}

	private void setMap() throws FormException {
		map = Imperator.getData().getMap(getPostInt("map"));
		if(map == null) {
			throw new FormException("Map does not exist");
		}
	}

	private void setColor() throws FormException {
		color = getPostString("color");
		if(!Arrays.asList(Imperator.getConfig().getStrings(Player.class, "color.hex")).contains(color)) {
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
