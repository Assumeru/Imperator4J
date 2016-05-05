package org.ee.i18n.gettext;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.ee.i18n.Language.TextDirection;
import org.ee.i18n.LanguageProvider;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;

public class GetTextProvider implements LanguageProvider {
	private static final Logger LOG = LogManager.createLogger();
	private final File path;

	public GetTextProvider(File path) {
		this.path = path;
	}

	@Override
	public GetText createLanguage(String lang, String locale, TextDirection direction) {
		return new GetText(loadMos(lang, locale), lang, locale, direction);
	}

	private Mo loadMos(String lang, String locale) {
		String langLocale = (lang + "-" + locale).toLowerCase(Locale.US);
		File dir = new File(path, langLocale);
		if(!dir.exists() || !dir.isDirectory()) {
			LOG.i(langLocale + " not found, falling back on " + lang);
			dir = new File(path, lang);
			if(!dir.exists() || !dir.isDirectory()) {
				LOG.w("No translations found for " + lang);
				return null;
			}
		}
		File[] files = dir.listFiles((file, name) -> name.toLowerCase().endsWith(".mo"));
		if(files == null) {
			return null;
		}
		Mo mo = null;
		for(File file : files) {
			try(InputStream input = new FileInputStream(file)) {
				Mo parsed = new MoParser(IOUtils.toByteArray(input)).parse();
				if(mo == null) {
					mo = parsed;
				} else {
					mo.merge(parsed);
				}
			} catch(Exception e) {
				LOG.w("Error parsing " + file, e);
			}
		}
		return mo;
	}
}
