package com.ee.imperator.api.handlers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;

import org.json.JSONObject;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.user.Member;

/**
 * Denotes an API endpoint.
 * <p>
 * An endpoint class must declare a constructor that either takes no arguments or takes one argument of type {@link ImperatorApplicationContext}.
 * <p>
 * API calls matching the annotated endpoint are delegated to one of its {@code handle} methods.
 * An endpoint may declare any number of methods named {@code handle} to receive different calls.
 * A {@code handle} method must return either {@code void} or {@link JSONObject}.
 * Every such method must take at least one argument of type {@link Member}, this is the user making the request.
 * Any further arguments must be annotated with {@link Param}.
 * The API delegates calls to the matching method with the highest number of arguments.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Endpoint {
	/**
	 * API call modes.
	 */
	enum Mode {
		CHAT, GAME, UPDATE;

		@Override
		public String toString() {
			return name().toLowerCase(Locale.US);
		}

		/**
		 * Returns a {@link Mode} matching the given string.
		 * 
		 * @param var The string to match
		 * @return A {@link Mode} or {@code null} if no match could be found
		 */
		public static Mode of(Object var) {
			if(var instanceof Mode) {
				return (Mode) var;
			}
			String name = String.valueOf(var).toUpperCase(Locale.US);
			for(Mode mode : values()) {
				if(mode.name().equals(name)) {
					return mode;
				}
			}
			return null;
		}
	}

	/**
	 * @return The mode parameter to match against an API call.
	 */
	Mode mode();

	/**
	 * @return The type parameter to match against an API call.
	 */
	String type();
}
