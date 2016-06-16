package org.ee.i18n.gettext;

import java.util.Arrays;
import java.util.Locale;

import org.ee.i18n.AbstractLanguage;

public class GetText extends AbstractLanguage {
	private final Mo mo;

	public GetText(Mo mo, Locale locale, TextDirection direction) {
		super(locale, direction);
		this.mo = mo;
	}

	@Override
	public String translate(Object... vars) {
		if(vars.length == 0) {
			throw new IllegalArgumentException("GetText.translate requires at least one argument");
		}
		return String.format(getTranslation(vars[0]), Arrays.copyOfRange(vars, 1, vars.length));
	}

	private String getTranslation(Object object) {
		if(mo == null) {
			return String.valueOf(object);
		} else if(object instanceof PluralString) {
			PluralString plural = (PluralString) object;
			return mo.translate(plural.getSingular(), plural.getPlural(), plural.getAmount());
		}
		return mo.translate(String.valueOf(object));
	}

	@Override
	public PluralString resolve(Object... vars) {
		if(vars.length < 3 || !(vars[2] instanceof Integer)) {
			throw new IllegalArgumentException("usage: resolve(String, String, int)");
		}
		return new PluralString(String.valueOf(vars[0]), String.valueOf(vars[1]), (Integer) vars[2]);
	}

	public Mo getMo() {
		return mo;
	}
}
