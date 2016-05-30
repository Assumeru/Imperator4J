package org.ee.expression.parser;

public class MultiplyToken extends BinaryToken {
	public MultiplyToken() {
		super(Precedence.MULT_DIVIDE, "*");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return lhs * rhs;
	}
}
