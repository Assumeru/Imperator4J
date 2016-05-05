package org.ee.i18n.gettext;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class Mo {
	private final Map<String, String[]> translations;
	private final int numPlurals;
	private final Function<Integer, Integer> pluralForm;
	private final String pluralFormString;

	public Mo(Map<String, String[]> translations, int numPlurals, Function<Integer, Integer> pluralForm, String pluralFormString) {
		this.translations = translations;
		this.numPlurals = numPlurals;
		this.pluralForm = pluralForm;
		this.pluralFormString = pluralFormString;
	}

	/**
	 * @return The number of translated strings contained in this MO
	 */
	public int getLength() {
		return translations.size();
	}

	/**
	 * @return The number of plural forms this language has
	 */
	public int getNumberOfPlurals() {
		return numPlurals;
	}

	/**
	 * @return A string representation of this language's plural forms formula
	 */
	public String getPluralForms() {
		return pluralFormString;
	}

	/**
	 * @return Translations by key
	 */
	public Map<String, String[]> getTranslations() {
		return Collections.unmodifiableMap(translations);
	}

	private String getKey(String singular, String context) {
		return context + ((char) 4) + singular;
	}

	private int getIndex(int amount) {
		int index = pluralForm.apply(amount);
		if(index < 0 || index >= numPlurals) {
			return 0;
		}
		return index;
	}

	/**
	 * Translates the given string.
	 * 
	 * @param singular The string to translate
	 * @return Either a translated string or the provided string
	 */
	public String translate(String singular) {
		return translate(singular, 0, singular);
	}

	/**
	 * Translates the given string.
	 * 
	 * @param context The context to find translations in
	 * @param singular The string to translate
	 * @return Either a translated string or the provided string
	 */
	public String translate(String context, String singular) {
		return translate(getKey(singular, context), 0, singular);
	}

	/**
	 * Translates the given string.
	 * 
	 * @param singular The string to translate
	 * @param plural The plural form of the string to translate
	 * @param amount The number to select a plural form for
	 * @return Either a translated string or the singular or plural depending on the given amount
	 */
	public String translate(String singular, String plural, int amount) {
		int index = getIndex(amount);
		return translate(singular, index, index == 0 ? singular : plural);
	}

	/**
	 * Translates the given string.
	 * 
	 * @param context The context to find translations in
	 * @param singular The string to translate
	 * @param plural The plural form of the string to translate
	 * @param amount The number to select a plural form for
	 * @return Either a translated string or the singular or plural depending on the given amount
	 */
	public String translate(String context, String singular, String plural, int amount) {
		int index = getIndex(amount);
		return translate(getKey(singular, context), getIndex(amount), index == 0 ? singular : plural);
	}

	private String translate(String key, int index, String fallback) {
		String[] translations = this.translations.get(key);
		if(translations != null && index < translations.length) {
			return translations[index];
		}
		return fallback;
	}

	/**
	 * Adds all translations to this MO.
	 * 
	 * @param other The MO to get translations from
	 */
	public void merge(Mo other) {
		translations.putAll(other.translations);
	}
}
