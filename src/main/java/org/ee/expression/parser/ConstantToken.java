package org.ee.expression.parser;

public class ConstantToken extends AbstractToken {
	public ConstantToken() {
		super(0);
	}

	@Override
	public int apply(int n) {
		throw new UnsupportedOperationException();
	}
}
