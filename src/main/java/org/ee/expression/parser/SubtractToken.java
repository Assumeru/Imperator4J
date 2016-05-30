package org.ee.expression.parser;

public class SubtractToken extends BinaryToken {
	public SubtractToken() {
		super(Precedence.ADD_SUBTRACT, "-");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return lhs - rhs;
	}
}
