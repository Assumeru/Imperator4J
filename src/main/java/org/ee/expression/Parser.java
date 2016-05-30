package org.ee.expression;

import java.text.ParseException;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.ee.collection.MapBuilder;
import org.ee.expression.parser.AddToken;
import org.ee.expression.parser.AndToken;
import org.ee.expression.parser.ConstantToken;
import org.ee.expression.parser.DivisionToken;
import org.ee.expression.parser.EqualLessToken;
import org.ee.expression.parser.EqualMoreToken;
import org.ee.expression.parser.EqualityToken;
import org.ee.expression.parser.InequalityToken;
import org.ee.expression.parser.IntegerToken;
import org.ee.expression.parser.LessToken;
import org.ee.expression.parser.ModuloToken;
import org.ee.expression.parser.MoreToken;
import org.ee.expression.parser.MultiplyToken;
import org.ee.expression.parser.NegateToken;
import org.ee.expression.parser.OrToken;
import org.ee.expression.parser.Scope;
import org.ee.expression.parser.SubtractToken;
import org.ee.expression.parser.Token;
import org.ee.expression.parser.VariableToken;

public class Parser {
	private static final Pattern WHITE_SPACE = Pattern.compile("\\s+");
	private static final Pattern EXPRESSION = Pattern.compile("^([\\sn\\=\\+\\-\\>\\<\\(\\)\\%\\*\\/\\!\\?\\:\\|\\&\\^0-9]+)$");
	public static final Token TERNARY_IF = new ConstantToken();
	public static final Token TERNARY_ELSE = new ConstantToken();
	public static final Token VARIABLE = new VariableToken();
	private static final Map<Object, Class<? extends Token>> TOKENS = new MapBuilder<Object, Class<? extends Token>>()
			.put('!', NegateToken.class)
			.put('>', MoreToken.class)
			.put('<', LessToken.class)
			.put('/', DivisionToken.class)
			.put('*', MultiplyToken.class)
			.put('%', ModuloToken.class)
			.put('+', AddToken.class)
			.put('-', SubtractToken.class)
			.put("==", EqualityToken.class)
			.put("!=", InequalityToken.class)
			.put(">=", EqualMoreToken.class)
			.put("<=", EqualLessToken.class)
			.put("||", OrToken.class)
			.put("&&", AndToken.class).build(true);
	private final String input;

	public Parser(String input) {
		if(!EXPRESSION.matcher(input).matches()) {
			throw new IllegalArgumentException(input + " contains illegal characters");
		}
		this.input = WHITE_SPACE.matcher(input).replaceAll("");
	}

	public Function<Integer, Integer> parse() throws ParseException {
		Scope current = new Scope();
		for(int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if(c == '(') {
				Scope open = new Scope(current);
				current.add(open);
				current = open;
			} else if(c == ')') {
				if(current.getParent() == null) {
					throw new ParseException("Brace mismatch", i);
				}
				current = current.getParent();
			} else if(c == '?') {
				current.add(TERNARY_IF);
			} else if(c == ':') {
				current.add(TERNARY_ELSE);
			} else if(c == 'n') {
				current.add(VARIABLE);
			} else if(isInt(c)) {
				int start = i;
				while(i < input.length() && isInt(input.charAt(i))) {
					i++;
				}
				current.add(parseInt(start, i));
				i--;
			} else if(i + 1 < input.length()) { 
				String key = input.substring(i, i + 2);
				if(TOKENS.containsKey(key)) {
					current.add(getToken(key));
					i++;
				} else if(TOKENS.containsKey(c)) {
					current.add(getToken(c));
				} else {
					throw new ParseException("Unknown token " + key, i);
				}
			} else if(TOKENS.containsKey(c)) {
				current.add(getToken(c));
			} else {
				throw new ParseException("Unknown token " + c, i);
			}
		}
		if(current.getParent() != null) {
			throw new ParseException("Brace mismatch", input.length());
		}
		current.resolve();
		return current.collapse();
	}

	private boolean isInt(char c) {
		return c >= '0' && c <= '9';
	}

	private Token parseInt(int start, int end) throws ParseException {
		try {
			return new IntegerToken(Integer.parseInt(input.substring(start, end)));
		} catch(NumberFormatException e) {
			throw new ParseException("Invalid int", start);
		}
	}

	private Token getToken(Object key) {
		try {
			return TOKENS.get(key).newInstance();
		} catch(InstantiationException | IllegalAccessException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
