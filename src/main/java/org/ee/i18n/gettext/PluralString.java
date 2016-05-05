package org.ee.i18n.gettext;

public class PluralString {
	private final String singular;
	private final String plural;
	private final int amount;

	public PluralString(String singular, String plural, int amount) {
		this.singular = singular;
		this.plural = plural;
		this.amount = amount;
	}

	@Override
	public String toString() {
		return plural;
	}

	public String getSingular() {
		return singular;
	}

	public String getPlural() {
		return plural;
	}

	public int getAmount() {
		return amount;
	}
}
