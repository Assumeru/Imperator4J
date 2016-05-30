package org.ee.expression.parser;

public class EqualLessToken extends BinaryToken {
	public EqualLessToken() {
		super(Precedence.RELATIONAL, "<=");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return toInt(lhs <= rhs);
	}
}
