package org.ee.expression.parser;

public class ModuloToken extends BinaryToken {
	public ModuloToken() {
		super(PREC_MULT_DIVIDE, "%");
	}

	@Override
	protected int apply(int lhs, int rhs) {
		return lhs % rhs;
	}
}
