package org.ee.config;

import java.io.Closeable;

public interface Config extends Closeable {
	Boolean getBoolean(String key);

	boolean getBoolean(String key, boolean fallback);

	Boolean getBoolean(Class<?> type, String key);

	boolean getBoolean(Class<?> type, String key, boolean fallback);

	Byte getByte(String key);

	byte getByte(String key, byte fallback);

	Byte getByte(Class<?> type, String key);

	byte getByte(Class<?> type, String key, byte fallback);

	Character getChar(String key);

	char getChar(String key, char fallback);

	Character getChar(Class<?> type, String key);

	char getChar(Class<?> type, String key, char fallback);

	Class<?> getClass(String key);

	Class<?> getClass(String key, Class<?> fallback);

	Class<?> getClass(Class<?> type, String key);

	Class<?> getClass(Class<?> type, String key, Class<?> fallback);

	Double getDouble(String key);

	double getDouble(String key, double fallback);

	Double getDouble(Class<?> type, String key);

	double getDouble(Class<?> type, String key, double fallback);

	Float getFloat(String key);

	float getFloat(String key, float fallback);

	Float getFloat(Class<?> type, String key);

	float getFloat(Class<?> type, String key, float fallback);

	Integer getInt(String key);

	int getInt(String key, int fallback);

	Integer getInt(Class<?> type, String key);

	int getInt(Class<?> type, String key, int fallback);

	Long getLong(String key);

	long getLong(String key, long fallback);

	Long getLong(Class<?> type, String key);

	long getLong(Class<?> type, String key, long fallback);

	Short getShort(String key);

	short getShort(String key, short fallback);

	Short getShort(Class<?> type, String key);

	short getShort(Class<?> type, String key, short fallback);

	String getString(String key);

	String getString(String key, String fallback);

	String getString(Class<?> type, String key);

	String getString(Class<?> type, String key, String fallback);

	String[] getStrings(String key);

	String[] getStrings(String key, String[] fallback);

	String[] getStrings(Class<?> type, String key);

	String[] getStrings(Class<?> type, String key, String[] fallback);

	static String getKey(Class<?> type, String key) {
		if(key == null || key.isEmpty()) {
			return type.getName();
		}
		return type.getName() + "." + key;
	}
}
