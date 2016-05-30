package org.ee.expression.parser;

public class VariableToken extends AbstractToken {
	public VariableToken() {
		super(Precedence.FIRST);
	}

	@Override
	public int apply(int n) {
		return n;
	}

	@Override
	public String toString() {
		return "n";
	}
}
