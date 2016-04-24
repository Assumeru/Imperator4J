package com.ee.imperator.crypt;

public interface PasswordHasher {
	String hash(String password);

	boolean matches(String hash, String password);
}
