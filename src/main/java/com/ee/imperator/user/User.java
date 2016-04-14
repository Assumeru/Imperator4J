package com.ee.imperator.user;

public interface User {
	int getId();

	String getName();

	String getProfileLink();

	default boolean equals(User other) {
		return getId() == other.getId();
	}
}
