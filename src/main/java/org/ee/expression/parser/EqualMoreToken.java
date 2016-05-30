package org.ee.expression.parser;

public class EqualMoreToken extends BinaryToken {
	public EqualMoreToken() {
		super(Precedence.RELATIONAL, ">=");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return toInt(lhs >= rhs);
	}
}
