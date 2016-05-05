package org.ee.expression;

import java.text.ParseException;
import java.util.function.Function;

import static org.hamcrest.core.Is.is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

public class TestParser {
	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Test
	public void test() throws ParseException {
		//Japanese
		test("0", n -> 0);
		//English
		test("n != 1", n -> i(n != 1));
		//French
		test("n > 1", n -> i(n > 1));
		//Latvian
		test("n%10==1 && n%100!=11 ? 0 : n != 0 ? 1 : 2", n -> n%10==1 && n%100!=11 ? 0 : n != 0 ? 1 : 2);
		//Gaeilge
		test("n==1 ? 0 : n==2 ? 1 : 2", n -> n==1 ? 0 : n==2 ? 1 : 2);
		//Romanian
		test("n==1 ? 0 : (n==0 || (n%100 > 0 && n%100 < 20)) ? 1 : 2", n -> n==1 ? 0 : (n==0 || (n%100 > 0 && n%100 < 20)) ? 1 : 2);
		//Lithuanian
		test("n%10==1 && n%100!=11 ? 0 : n%10>=2 && (n%100<10 || n%100>=20) ? 1 : 2", n -> n%10==1 && n%100!=11 ? 0 : n%10>=2 && (n%100<10 || n%100>=20) ? 1 : 2);
		//Russian
		test("n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2", n -> n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2);
		//Slovak
		test("(n==1) ? 0 : (n>=2 && n<=4) ? 1 : 2", n -> (n==1) ? 0 : (n>=2 && n<=4) ? 1 : 2);
		//Polish
		test("n==1 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2", n -> n==1 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2);
		//Slovenian
		test("n%100==1 ? 0 : n%100==2 ? 1 : n%100==3 || n%100==4 ? 2 : 3", n -> n%100==1 ? 0 : n%100==2 ? 1 : n%100==3 || n%100==4 ? 2 : 3);
		//Arabic
		test("n==0 ? 0 : n==1 ? 1 : n==2 ? 2 : n%100>=3 && n%100<=10 ? 3 : n%100>=11 ? 4 : 5", n -> n==0 ? 0 : n==1 ? 1 : n==2 ? 2 : n%100>=3 && n%100<=10 ? 3 : n%100>=11 ? 4 : 5);
	}

	private void test(String expression, Function<Integer, Integer> function) throws ParseException {
		Function<Integer, Integer> parsed = new Parser(expression).parse();
		System.out.println(parsed);
		for(int i = 0; i < 10000; i++) {
			int a = function.apply(i);
			int b = parsed.apply(i);
			collector.checkThat(i + " expected " + a + " got " + b, a, is(b));
		}
	}

	private static int i(boolean b) {
		return b ? 1 : 0;
	}
}
