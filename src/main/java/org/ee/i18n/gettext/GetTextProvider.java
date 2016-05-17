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
	public GetText getLanguage(Locale locale, TextDirection direction) {
		return new GetText(loadMos(locale), locale, direction);
	}

	private Mo loadMos(Locale locale) {
		String[] depth = locale.toLanguageTag().toLowerCase(Locale.US).split("-");
		File dir = new File(path, depth[0]);
		for(int i = 1; i < depth.length; i++) {
			File sub = new File(dir, depth[i]);
			if(sub.exists()) {
				dir = sub;
			} else {
				LOG.i(sub + " not found, falling back on " + dir);
				break;
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
