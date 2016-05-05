package org.ee.expression.parser;

public class InequalityToken extends BinaryToken {
	public InequalityToken() {
		super(PREC_EQUALITY, "!=");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return toInt(lhs != rhs);
	}
}
