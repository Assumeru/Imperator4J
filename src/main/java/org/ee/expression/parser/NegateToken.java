package org.ee.expression.parser;

import java.text.ParseException;
import java.util.List;

public class NegateToken extends AbstractToken {
	private Token value;

	public NegateToken() {
		super(Precedence.UNARY);
	}

	@Override
	public int apply(int n) {
		return toInt(!toBoolean(value.apply(n)));
	}

	@Override
	public int resolve(int index, List<Token> tokens) throws ParseException {
		if(!isResolved()) {
			resolve();
			if(index + 1 < tokens.size()) {
				value = tokens.remove(index + 1);
				return value.resolve(index, tokens);
			} else {
				throw new ParseException("No value to negate", index);
			}
		}
		return index;
	}

	@Override
	public NegateToken collapse() throws ParseException {
		value = value.collapse();
		return this;
	}

	@Override
	public String toString() {
		return "!" + value;
	}
}
