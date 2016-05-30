package org.ee.expression.parser;

public class AndToken extends BinaryToken {
	public AndToken() {
		super(Precedence.AND, "&&");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return toInt(toBoolean(lhs) && toBoolean(rhs));
	}
}
