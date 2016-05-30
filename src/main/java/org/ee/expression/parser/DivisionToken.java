package org.ee.expression.parser;

public class DivisionToken extends BinaryToken {
	public DivisionToken() {
		super(Precedence.MULT_DIVIDE, "/");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return lhs / rhs;
	}
}
