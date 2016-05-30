package org.ee.expression.parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.ee.expression.Parser;

public class Scope extends AbstractToken {
	private Scope parent;
	private List<Token> tokens;

	public Scope(Scope parent) {
		super(Precedence.FIRST);
		this.parent = parent;
		tokens = new ArrayList<>();
	}

	public Scope() {
		this(null);
	}

	public void add(Token token) {
		tokens.add(token);
	}

	public Scope getParent() {
		return parent;
	}

	@Override
	public int apply(int n) {
		throw new IllegalStateException("Uncollapsed scope");
	}

	@Override
	public Token collapse() throws ParseException {
		checkExpression();
		return tokens.get(0).collapse();
	}

	private void checkExpression() throws ParseException {
		if(tokens.size() != 1) {
			throw new ParseException("Scope does not resolve to a single expression", -1);
		}
	}

	@Override
	public int resolve(int index, List<Token> tokens) throws ParseException {
		if(!isResolved()) {
			resolve();
		}
		return index;
	}

	public void resolve() throws ParseException {
		super.resolve();
		int next = Integer.MIN_VALUE;
		int min;
		do {
			min = next;
			for(int i = 0; i < tokens.size(); i++) {
				Token token = tokens.get(i);
				if(token.getPrecedence() <= min) {
					i = token.resolve(i, tokens);
				} else if(next != min) {
					next = Math.min(next, token.getPrecedence());
				} else {
					next = token.getPrecedence();
				}
			}
		} while(next != min);
		parseTenaryScope(tokens);
		checkExpression();
	}

	private void parseTenaryScope(List<Token> tokens) throws ParseException {
		if(tokens.size() <= 4) {
			return;
		} else if(tokens.get(1) != Parser.TERNARY_IF) {
			throw new ParseException("? not found", -1);
		}
		Scope resultTrue = new Scope();
		Scope resultFalse = new Scope();
		ConditionalToken token = new ConditionalToken(tokens.get(0), resultTrue, resultFalse);
		Scope current = resultTrue;
		int depth = 0;
		for(int i = 2; i < tokens.size(); i++) {
			Token t = tokens.get(i);
			if(t == Parser.TERNARY_IF) {
				depth++;
			} else if(t == Parser.TERNARY_ELSE) {
				if(depth == 0) {
					if(current == resultTrue) {
						current = resultFalse;
						continue;
					} else {
						throw new ParseException("Conditional mismatch", i);
					}
				}
				depth--;
			}
			current.add(t);
		}
		parseTenaryScope(resultTrue.tokens);
		parseTenaryScope(resultFalse.tokens);
		tokens.clear();
		tokens.add(token);
	}

	@Override
	public String toString() {
		return "(" + tokens + ")";
	}
}
