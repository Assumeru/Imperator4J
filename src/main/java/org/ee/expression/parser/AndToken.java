package org.ee.expression.parser;

public class AndToken extends BinaryToken {
	public AndToken() {
		super(PREC_AND, "&&");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return toInt(toBoolean(lhs) && toBoolean(rhs));
	}
}
