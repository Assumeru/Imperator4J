package com.ee.imperator.crypt.bcrypt;

import org.mindrot.jbcrypt.BCrypt;

import com.ee.imperator.Imperator;
import com.ee.imperator.crypt.PasswordHasher;

public class BCryptHasher implements PasswordHasher {
	private final int rounds = Imperator.getConfig().getInt(getClass(), "rounds", 10);

	@Override
	public String hash(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt(rounds));
	}

	@Override
	public boolean matches(String hash, String password) {
		return BCrypt.checkpw(password, hash);
	}
}