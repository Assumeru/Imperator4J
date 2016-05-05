package org.ee.i18n.none;

import java.util.Arrays;

import org.ee.i18n.AbstractLanguage;

public class DefaultLanguage extends AbstractLanguage {
	public DefaultLanguage(String lang, String locale, TextDirection direction) {
		super(lang, locale, direction);
	}

	@Override
	public CharSequence translate(Object... vars) {
		if(vars.length == 0) {
			throw new IllegalArgumentException("DefaultLanguage.translate requires at least one argument");
		}
		return String.format(vars[0].toString(), Arrays.copyOfRange(vars, 1, vars.length));
	}

	@Override
	public Object resolve(Object... vars) {
		if(vars.length == 0) {
			throw new IllegalArgumentException("DefaultLanguage.resolve requires at least one argument");
		}
		return String.valueOf(vars[vars.length - 1]);
	}
}
