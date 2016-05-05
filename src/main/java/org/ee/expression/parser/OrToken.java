package org.ee.expression.parser;

public class OrToken extends BinaryToken {
	public OrToken() {
		super(PREC_OR, "||");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return toInt(toBoolean(lhs) || toBoolean(rhs));
	}
}
