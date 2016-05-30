package org.ee.expression.parser;

public class EqualityToken extends BinaryToken {
	public EqualityToken() {
		super(Precedence.EQUALITY, "==");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return toInt(lhs == rhs);
	}
}
