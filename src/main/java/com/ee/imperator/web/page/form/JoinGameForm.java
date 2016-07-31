package com.ee.imperator.web.page.form;

import java.util.Map;

import com.ee.imperator.exception.FormException;
import com.ee.imperator.game.Game;
import com.ee.imperator.web.context.PageContext;

public class JoinGameForm extends Form {
	private final Game game;
	private String color;

	public JoinGameForm(PageContext context, Game game, Map<String, String> colors) throws FormException {
		super(context);
		this.game = game;
		setColor(colors);
		validatePassword();
	}

	private void setColor(Map<String, String> colors) throws FormException {
		color = getPostString("color");
		if(!colors.containsValue(color)) {
			throw new FormException("This color is already in use", "colorError");
		}
	}

	private void validatePassword() throws FormException {
		if(game.hasPassword()) {
			String password = getPostString("password");
			String code = getPostString("code");
			if(password != null && context.getHasher().matches(game.getPassword(), password)) {
				return;
			} else if(code != null) {
				if(code.equals(game.getInviteCode(context))) {
					return;
				}
				throw new FormException("The code you entered was incorrect", "passwordError");
			}
			throw new FormException("The password you entered was incorrect", "passwordError");
		}
	}

	public String getColor() {
		return color;
	}
}
