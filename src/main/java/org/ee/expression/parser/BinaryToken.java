package org.ee.expression.parser;

import java.text.ParseException;
import java.util.List;

public abstract class BinaryToken extends AbstractToken {
	private Token lhs;
	private Token rhs;
	private String operator;

	public BinaryToken(Precedence precedence, String operator) {
		super(precedence);
		this.operator = " " + operator + " ";
	}

	@Override
	public int apply(int n) {
		return apply(lhs.apply(n), rhs.apply(n));
	}

	protected abstract int apply(int lhs, int rhs);

	@Override
	public int resolve(int index, List<Token> tokens) throws ParseException {
		if(!isResolved()) {
			resolve();
			if(index > 0 && index + 1 < tokens.size()) {
				rhs = tokens.remove(index + 1);
				lhs = tokens.remove(index - 1);
				return rhs.resolve(index, tokens) - 1;
			} else {
				throw new ParseException(operator + " without arguments", index);
			}
		}
		return index;
	}

	@Override
	public BinaryToken collapse() throws ParseException {
		lhs = lhs.collapse();
		rhs = rhs.collapse();
		return this;
	}

	@Override
	public String toString() {
		return "(" + lhs + operator + rhs + ")";
	}
}
