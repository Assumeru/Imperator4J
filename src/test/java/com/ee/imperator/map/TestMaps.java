package com.ee.imperator.map;

import java.io.File;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import com.ee.imperator.data.xml.MapParser;

public class TestMaps {
	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Test
	public void test() {
		String path = getClass().getClassLoader().getResource(".").getPath() + "../../src/main/webapp/WEB-INF/maps/";
		for(File file : new File(path).listFiles((d, n) -> n.endsWith(".xml"))) {
			test(file);
		}
	}

	private void test(final File file) {
		collector.checkSucceeds(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					Map map = new MapParser(file).parse();
					test(map);
				} catch(Exception e) {
					throw new Exception("Invalid map: " + file, e);
				}
				return null;
			}
		});
	}

	private void test(Map map) {
		Assert.assertTrue("Mission distribution < players", map.getMissionDistribution().size() >= map.getPlayers());
		for(Integer id : map.getMissionDistribution()) {
			Assert.assertTrue("Unknown mission " + id, map.getMissions().containsKey(id));
		}
		Assert.assertFalse("Map has no name", map.getName().isEmpty());
		Assert.assertTrue("Players < 2", map.getPlayers() > 1);
		for(Region region : map.getRegions().values()) {
			Assert.assertFalse(region.getId() + " has no name", region.getName().isEmpty());
		}
		for(Territory territory : map.getTerritories().values()) {
			Assert.assertFalse(territory.getId() + " has no name", territory.getName().isEmpty());
			Assert.assertFalse(territory.getId() + " borders itself", territory.getBorders().contains(territory));
			Assert.assertTrue(territory.getId() + " has no borders", territory.getBorders().size() > 0);
		}
	}
}
