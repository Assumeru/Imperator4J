package org.ee.expression.parser;

public class MoreToken extends BinaryToken {
	public MoreToken() {
		super(Precedence.RELATIONAL, ">");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return toInt(lhs > rhs);
	}
}
