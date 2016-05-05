package org.ee.expression.parser;

public class MoreToken extends BinaryToken {
	public MoreToken() {
		super(PREC_RELATIONAL, ">");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return toInt(lhs > rhs);
	}
}
