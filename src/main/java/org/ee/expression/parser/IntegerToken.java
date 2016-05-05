package org.ee.expression.parser;

import java.text.ParseException;

public class IntegerToken extends AbstractToken {
	private final int value;

	public IntegerToken(int value) {
		super(0);
		this.value = value;
	}

	@Override
	public int apply(int n) throws ParseException {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
