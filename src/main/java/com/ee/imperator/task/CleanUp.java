package com.ee.imperator.task;

import java.util.Collection;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.task.AbstractTask;

import com.ee.imperator.ImperatorApplicationContext;

public class CleanUp extends AbstractTask {
	private static final Logger LOG = LogManager.createLogger();
	private final ImperatorApplicationContext context;
	private final long maxFinishedGameAge;
	private final long inactiveGameTime;
	private final long maxChatMessageAge;
	private final int numberOfMessagesToKeep;
	private final long sleep;

	public CleanUp(ImperatorApplicationContext context) {
		this.context = context;
		maxFinishedGameAge = context.getLongSetting(getClass(), "maxFinishedGameAge");
		inactiveGameTime = context.getLongSetting(getClass(), "inactiveGameTime");
		maxChatMessageAge = context.getLongSetting(getClass(), "maxChatMessageAge");
		numberOfMessagesToKeep = context.getIntSetting(getClass(), "numberOfMessagesToKeep");
		sleep = context.getLongSetting(getClass(), "sleep");
	}

	@Override
	protected void work() {
		long time = System.currentTimeMillis();
		Collection<Integer> games = context.getState().deleteOldGames(time - maxFinishedGameAge, time - inactiveGameTime);
		LOG.i("Deleted " + games.size() + " games");
		int messages = context.getState().deleteOldMessages(time - maxChatMessageAge, numberOfMessagesToKeep);
		LOG.i("Deleted " + messages + " chat messages");
		try {
			Thread.sleep(sleep);
		} catch(InterruptedException e) {
			LOG.v(e);
			running = false;
		}
	}
}
