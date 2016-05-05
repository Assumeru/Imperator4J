package org.ee.expression.parser;

public class SubtractToken extends BinaryToken {
	public SubtractToken() {
		super(PREC_ADD_SUBTRACT, "-");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return lhs - rhs;
	}
}
