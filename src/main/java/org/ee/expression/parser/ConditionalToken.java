package org.ee.expression.parser;

import java.text.ParseException;

public class ConditionalToken extends AbstractToken {
	private Token condition;
	private Token resultTrue;
	private Token resultFalse;

	public ConditionalToken(Token condition, Token resultTrue, Token resultFalse) {
		super(Precedence.TERNARY_IF);
		this.condition = condition;
		this.resultTrue = resultTrue;
		this.resultFalse = resultFalse;
	}

	@Override
	public int apply(int n) {
		return toBoolean(condition.apply(n)) ? resultTrue.apply(n) : resultFalse.apply(n);
	}

	@Override
	public ConditionalToken collapse() throws ParseException {
		condition = condition.collapse();
		resultTrue = resultTrue.collapse();
		resultFalse = resultFalse.collapse();
		return this;
	}

	@Override
	public String toString() {
		return "(" + condition + " ? " + resultTrue + " : " + resultFalse + ")";
	}
}
