package org.ee.expression.parser;

public class LessToken extends BinaryToken {
	public LessToken() {
		super(Precedence.RELATIONAL, "<");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return toInt(lhs < rhs);
	}
}
