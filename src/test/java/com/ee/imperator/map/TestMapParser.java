package com.ee.imperator.map;

import java.io.File;
import java.util.concurrent.Callable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import com.ee.imperator.data.xml.MapParser;

public class TestMapParser {
	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Test
	public void test() {
		String path = getClass().getClassLoader().getResource(".").getPath() + "../../src/main/webapp/WEB-INF/maps/";
		for(int i = 0; i <= 6; i++) {
			test(new File(path + i + ".xml"));
		}
	}

	private void test(final File file) {
		collector.checkSucceeds(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				System.out.println(file);
				Map map = new MapParser(file).parse();
				System.out.println(map.getName());
				return null;
			}
		});
	}
}
