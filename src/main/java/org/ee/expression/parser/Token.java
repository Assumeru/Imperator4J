package org.ee.expression.parser;

import java.text.ParseException;
import java.util.List;
import java.util.function.Function;

public interface Token extends Function<Integer, Integer> {
	enum Precedence {
		FIRST, UNARY, MULT_DIVIDE, ADD_SUBTRACT, RELATIONAL, EQUALITY, AND, OR, TERNARY_IF
	}

	/**
	 * Calculates f(n) for this token.
	 * 
	 * @param n The value of variable n
	 * @return The result of applying $t to this token
	 */
	int apply(int n);

	/**
	 * Removes superfluous tokens from the tree.
	 * 
	 * @return The first relevant token in this tree
	 * @throws ParseException 
	 */
	Token collapse() throws ParseException;

	/**
	 * @return Resolution precedence, lower values are resolved first
	 */
	int getPrecedence();

	/**
	 * @return True if the resolve method has been called
	 */
	boolean isResolved();

	/**
	 * Allows this token to modify its scope.
	 * 
	 * @param index The current token index
	 * @param tokens The tokens being parsed
	 * @return The new index
	 * @throws ParseException If this token cannot be resolved
	 */
	int resolve(int index, List<Token> tokens) throws ParseException;
}
