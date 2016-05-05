package org.ee.expression.parser;

import java.text.ParseException;

public class ConstantToken extends AbstractToken {
	public ConstantToken() {
		super(0);
	}

	@Override
	public int apply(int n) throws ParseException {
		throw new UnsupportedOperationException();
	}
}
