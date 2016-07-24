package com.ee.imperator.task;

import java.util.Collection;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;

import com.ee.imperator.Imperator;
import com.ee.imperator.exception.ConfigurationException;

public class CleanUp implements Runnable {
	private static final Logger LOG = LogManager.createLogger();
	private final long maxFinishedGameAge;
	private final long inactiveGameTime;
	private final long maxChatMessageAge;
	private final int numberOfMessagesToKeep;
	private final long sleep;
	private boolean running;
	private Thread thread;

	public CleanUp() {
		maxFinishedGameAge = getLongSetting("maxFinishedGameAge");
		inactiveGameTime = getLongSetting("inactiveGameTime");
		maxChatMessageAge = getLongSetting("maxChatMessageAge");
		numberOfMessagesToKeep = getIntSetting("numberOfMessagesToKeep");
		sleep = getLongSetting("sleep");
	}

	private long getLongSetting(String key) {
		Long value = Imperator.getConfig().getLong(CleanUp.class, key);
		if(value == null) {
			throw new ConfigurationException("Missing config value for " + key);
		}
		return value;
	}

	private int getIntSetting(String key) {
		Integer value = Imperator.getConfig().getInt(CleanUp.class, key);
		if(value == null) {
			throw new ConfigurationException("Missing config value for " + key);
		}
		return value;
	}

	@Override
	public void run() {
		while(running) {
			long time = System.currentTimeMillis();
			Collection<Integer> games = Imperator.getState().deleteOldGames(time - maxFinishedGameAge, time - inactiveGameTime);
			LOG.i("Deleted " + games.size() + " games");
			int messages = Imperator.getState().deleteOldMessages(time - maxChatMessageAge, numberOfMessagesToKeep);
			LOG.i("Deleted " + messages + " chat messages");
			try {
				Thread.sleep(sleep);
			} catch(InterruptedException e) {
				LOG.v(e);
				running = false;
			}
		}
	}

	public void start() {
		if(!running) {
			running = true;
			thread = new Thread(this, getClass().getName());
			thread.setDaemon(true);
			thread.start();
		}
	}

	public void stop() {
		running = false;
		thread.interrupt();
	}
}
