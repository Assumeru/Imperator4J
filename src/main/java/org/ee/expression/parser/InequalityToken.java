package org.ee.expression.parser;

public class InequalityToken extends BinaryToken {
	public InequalityToken() {
		super(Precedence.EQUALITY, "!=");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return toInt(lhs != rhs);
	}
}
