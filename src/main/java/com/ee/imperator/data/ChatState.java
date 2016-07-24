package com.ee.imperator.data;

import java.io.Closeable;
import java.util.List;

import com.ee.imperator.chat.ChatMessage;
import com.ee.imperator.exception.TransactionException;

/**
 * This interface provides a way to store and retrieve chat messages.
 * <p>
 * The reserved game id {@code 0} denotes the global chat.
 */
public interface ChatState extends Closeable {
	/**
	 * Gets messages made after a specified time.
	 * 
	 * @param id The game to get messages for
	 * @param time The minimum message age (exclusive)
	 * @return A list of messages
	 */
	List<ChatMessage> getChatMessages(int id, long time);

	/**
	 * Checks if new messages are available for a given game.
	 * 
	 * @param id The game to check for messages
	 * @param time The minimum message age (exclusive)
	 * @return True if the specified game has messages after the specified time
	 */
	boolean hasChatMessages(int id, long time);

	/**
	 * Saves a message.
	 * 
	 * @param message The message to save
	 * @throws TransactionException If the message cannot be saved
	 */
	void addMessage(ChatMessage message) throws TransactionException;

	/**
	 * Deletes a message.
	 * 
	 * @param gid Game to delete the message from
	 * @param time Time of the message to delete
	 * @throws TransactionException If the message cannot be deleted
	 */
	void deleteMessage(int gid, long time) throws TransactionException;

	/**
	 * Removes old messages from the global chat.
	 * 
	 * @param time Maximum message age (exclusive)
	 * @param keep Number of messages to keep anyway
	 * @return The number of deleted messages
	 */
	int deleteOldMessages(long time, int keep);
}
