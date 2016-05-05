package org.ee.expression.parser;

import java.text.ParseException;
import java.util.List;
import java.util.function.Function;

public interface Token extends Function<Integer, Integer> {
	public static final int PREC_UNARY = 1;
	public static final int PREC_MULT_DIVIDE = 2;
	public static final int PREC_ADD_SUBTRACT = 3;
	public static final int PREC_RELATIONAL = 5;
	public static final int PREC_EQUALITY = 6;
	public static final int PREC_AND = 10;
	public static final int PREC_OR = 11;
	public static final int PREC_TERNARY_IF = 12;

	/**
	 * Calculates f(n) for this token.
	 * 
	 * @param n The value of variable n
	 * @return The result of applying $t to this token
	 * @throws ParseException 
	 */
	int apply(int n) throws ParseException;

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
