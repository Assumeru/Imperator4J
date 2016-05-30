package org.ee.expression.parser;

public class IntegerToken extends AbstractToken {
	private final int value;

	public IntegerToken(int value) {
		super(Precedence.FIRST);
		this.value = value;
	}

	@Override
	public int apply(int n) {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
