package org.ee.expression.parser;

import java.text.ParseException;
import java.util.List;

public abstract class AbstractToken implements Token {
	private final int precedence;
	private boolean resolved;

	public AbstractToken(Precedence precedence) {
		this.precedence = precedence.ordinal();
	}

	@Override
	public Integer apply(Integer n) {
		return apply(n.intValue());
	}

	@Override
	public Token collapse() throws ParseException {
		return this;
	}

	@Override
	public int getPrecedence() {
		return precedence;
	}

	@Override
	public boolean isResolved() {
		return resolved;
	}

	@Override
	public int resolve(int index, List<Token> tokens) throws ParseException {
		resolve();
		return index;
	}

	protected void resolve() throws ParseException {
		resolved = true;
	}

	static boolean toBoolean(int i) {
		return i != 0;
	}

	static int toInt(boolean b) {
		return b ? 1 : 0;
	}
}
