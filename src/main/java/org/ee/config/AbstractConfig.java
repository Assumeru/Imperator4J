package org.ee.config;

import java.io.IOException;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;

public abstract class AbstractConfig implements Config {
	private static final Logger LOG = LogManager.createLogger();

	@Override
	public Boolean getBoolean(String key) {
		String value = getString(key);
		if(value != null) {
			if("true".equalsIgnoreCase(value)) {
				return true;
			} else if("false".equalsIgnoreCase(value)) {
				return false;
			}
		}
		return null;
	}

	@Override
	public boolean getBoolean(String key, boolean fallback) {
		Boolean value = getBoolean(key);
		if(value != null) {
			return value;
		}
		LOG.d("No value found for " + key);
		return fallback;
	}

	@Override
	public Boolean getBoolean(Class<?> type, String key) {
		return getBoolean(Config.getKey(type, key));
	}

	@Override
	public boolean getBoolean(Class<?> type, String key, boolean fallback) {
		return getBoolean(Config.getKey(type, key), fallback);
	}

	@Override
	public Byte getByte(String key) {
		String value = getString(key);
		if(value != null) {
			try {
				return Byte.parseByte(value);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	@Override
	public byte getByte(String key, byte fallback) {
		Byte value = getByte(key);
		if(value != null) {
			return value;
		}
		LOG.d("No value found for " + key);
		return fallback;
	}

	@Override
	public Byte getByte(Class<?> type, String key) {
		return getByte(Config.getKey(type, key));
	}

	@Override
	public byte getByte(Class<?> type, String key, byte fallback) {
		return getByte(Config.getKey(type, key), fallback);
	}

	@Override
	public Character getChar(String key) {
		String value = getString(key);
		if(value != null && value.length() == 1) {
			return value.charAt(0);
		}
		return null;
	}

	@Override
	public char getChar(String key, char fallback) {
		Character value = getChar(key);
		if(value != null) {
			return value;
		}
		LOG.d("No value found for " + key);
		return fallback;
	}

	@Override
	public Character getChar(Class<?> type, String key) {
		return getChar(Config.getKey(type, key));
	}

	@Override
	public char getChar(Class<?> type, String key, char fallback) {
		return getChar(Config.getKey(type, key), fallback);
	}

	@Override
	public Class<?> getClass(String key) {
		String value = getString(key);
		if(value != null) {
			try {
				return Class.forName(value);
			} catch (ClassNotFoundException e) {
			}
		}
		return null;
	}

	@Override
	public Class<?> getClass(String key, Class<?> fallback) {
		Class<?> value = getClass(key);
		if(value != null) {
			return value;
		}
		LOG.d("No value found for " + key);
		return fallback;
	}

	@Override
	public Class<?> getClass(Class<?> type, String key) {
		return getClass(Config.getKey(type, key));
	}

	@Override
	public Class<?> getClass(Class<?> type, String key, Class<?> fallback) {
		return getClass(Config.getKey(type, key), fallback);
	}

	@Override
	public Double getDouble(String key) {
		String value = getString(key);
		if(value != null) {
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	@Override
	public double getDouble(String key, double fallback) {
		Double value = getDouble(key);
		if(value != null) {
			return value;
		}
		LOG.d("No value found for " + key);
		return fallback;
	}

	@Override
	public Double getDouble(Class<?> type, String key) {
		return getDouble(Config.getKey(type, key));
	}

	@Override
	public double getDouble(Class<?> type, String key, double fallback) {
		return getDouble(Config.getKey(type, key), fallback);
	}

	@Override
	public Float getFloat(String key) {
		String value = getString(key);
		if(value != null) {
			try {
				return Float.parseFloat(value);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	@Override
	public float getFloat(String key, float fallback) {
		Float value = getFloat(key);
		if(value != null) {
			return value;
		}
		LOG.d("No value found for " + key);
		return fallback;
	}

	@Override
	public Float getFloat(Class<?> type, String key) {
		return getFloat(Config.getKey(type, key));
	}

	@Override
	public float getFloat(Class<?> type, String key, float fallback) {
		return getFloat(Config.getKey(type, key), fallback);
	}

	@Override
	public Integer getInt(String key) {
		String value = getString(key);
		if(value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	@Override
	public int getInt(String key, int fallback) {
		Integer value = getInt(key);
		if(value != null) {
			return value;
		}
		LOG.d("No value found for " + key);
		return fallback;
	}

	@Override
	public Integer getInt(Class<?> type, String key) {
		return getInt(Config.getKey(type, key));
	}

	@Override
	public int getInt(Class<?> type, String key, int fallback) {
		return getInt(Config.getKey(type, key), fallback);
	}

	@Override
	public Long getLong(String key) {
		String value = getString(key);
		if(value != null) {
			try {
				return Long.parseLong(value);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	@Override
	public long getLong(String key, long fallback) {
		Long value = getLong(key);
		if(value != null) {
			return value;
		}
		LOG.d("No value found for " + key);
		return fallback;
	}

	@Override
	public Long getLong(Class<?> type, String key) {
		return getLong(Config.getKey(type, key));
	}

	@Override
	public long getLong(Class<?> type, String key, long fallback) {
		return getLong(Config.getKey(type, key), fallback);
	}

	@Override
	public Short getShort(String key) {
		String value = getString(key);
		if(value != null) {
			try {
				return Short.parseShort(value);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	@Override
	public short getShort(String key, short fallback) {
		Short value = getShort(key);
		if(value != null) {
			return value;
		}
		LOG.d("No value found for " + key);
		return fallback;
	}

	@Override
	public Short getShort(Class<?> type, String key) {
		return getShort(Config.getKey(type, key));
	}

	@Override
	public short getShort(Class<?> type, String key, short fallback) {
		return getShort(Config.getKey(type, key), fallback);
	}

	@Override
	public String getString(String key, String fallback) {
		String value = getString(key);
		if(value != null) {
			return value;
		}
		LOG.d("No value found for " + key);
		return fallback;
	}

	@Override
	public String getString(Class<?> type, String key) {
		return getString(Config.getKey(type, key));
	}

	@Override
	public String getString(Class<?> type, String key, String fallback) {
		return getString(Config.getKey(type, key), fallback);
	}

	@Override
	public String[] getStrings(String key) {
		String value = getString(key);
		if(value != null) {
			return value.split("\\s*,\\s*");
		}
		return null;
	}

	@Override
	public String[] getStrings(String key, String[] fallback) {
		String[] value = getStrings(key);
		if(value != null) {
			return value;
		}
		LOG.d("No value found for " + key);
		return fallback;
	}

	@Override
	public String[] getStrings(Class<?> type, String key) {
		return getStrings(Config.getKey(type, key));
	}

	@Override
	public String[] getStrings(Class<?> type, String key, String[] fallback) {
		return getStrings(Config.getKey(type, key), fallback);
	}

	@Override
	public void close() throws IOException {
	}
}
