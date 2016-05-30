package org.ee.expression.parser;

public class AddToken extends BinaryToken {
	public AddToken() {
		super(Precedence.ADD_SUBTRACT, "+");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return lhs + rhs;
	}
}
