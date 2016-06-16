package org.ee.i18n.gettext;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ee.expression.Parser;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;

public class MoParser {
	private static final Logger LOG = LogManager.createLogger();
	private static final int MAGIC = 0x950412de;
	private static final Pattern PLURAL_FORMS = Pattern.compile("^\\s*nplurals\\s*=\\s*(\\d+)\\s*;\\s*plural\\s*=\\s*(.+)$");
	private static final Function<Integer, Integer> DEFAULT_PLURAL_FORM = n -> n != 0 ? 1 : 0;
	public static final String DEFAULT_PLURAL_FORM_STRING = "n != 0";
	private final ByteBuffer input;
	private Charset charset;
	private boolean overrideCharset;
	private int revision;
	private int numStrings;
	private int originalTable;
	private int translationTable;
	private Map<String, String> headers;
	private Map<String, String[]> translations;
	private int numPlurals = 2;
	private Function<Integer, Integer> pluralForm = DEFAULT_PLURAL_FORM;
	private String pluralFormString = DEFAULT_PLURAL_FORM_STRING;

	public MoParser(byte[] input) {
		this(input, getDefaultCharset());
		overrideCharset = true;
	}

	public MoParser(byte[] input, Charset charset) {
		this.input = ByteBuffer.wrap(input).order(ByteOrder.LITTLE_ENDIAN);
		this.charset = charset == null ? getDefaultCharset() : charset;
		headers = new HashMap<>();
		translations = new HashMap<>();
	}

	public Mo parse() throws ParseException {
		parseEndianess();
		parseRevision();
		numStrings = input.getInt();
		originalTable = input.getInt();
		translationTable = input.getInt();
		parseTranslations();
		parsePluralForms();
		return new Mo(translations, numPlurals, pluralForm, pluralFormString);
	}

	private void parseEndianess() throws ParseException {
		if(input.getInt() != MAGIC) {
			input.rewind();
			input.order(ByteOrder.BIG_ENDIAN);
			if(input.getInt() != MAGIC) {
				throw new ParseException("Invalid magic number", input.position());
			}
		}
	}

	private void parseRevision() {
		revision = input.getInt();
		if(revision != 0) {
			throw new UnsupportedOperationException("Unsupported revision " + revision);
		}
	}

	private void parseTranslations() {
		for(int i = 0; i < numStrings; i++) {
			input.position(originalTable + 8 * i);
			byte[] original = new byte[input.getInt()];
			int originalOffset = input.getInt();
			input.position(translationTable + 8 * i);
			byte[] translation = new byte[input.getInt()];
			int translationOffset = input.getInt();
			input.position(originalOffset);
			input.get(original);
			input.position(translationOffset);
			input.get(translation);
			addEntry(original, translation);
		}
	}

	private void addEntry(byte[] original, byte[] translation) {
		if(original.length == 0) {
			//Entries are sorted, "" is always first
			parseHeaders(translation);
		} else {
			//Split plural forms
			translations.put(new String(original, charset).split("\0")[0], new String(translation, charset).split("\0"));
		}
	}

	private void parseHeaders(byte[] translation) {
		//Replace fake newlines
		String headers = new String(translation, charset).replace("\\n", "\n");
		for(String header : headers.split("\n")) {
			String[] keyValue = header.split(":", 2);
			if(keyValue.length > 1) {
				String key = keyValue[0].trim();
				String value = keyValue[1].trim();
				this.headers.put(key, value);
				if(overrideCharset && "Content-Type".equals(key)) {
					trySetCharset(value);
				}
			}
		}
	}

	private void trySetCharset(String contentType) {
		String[] parts = contentType.toLowerCase(Locale.US).split("charset\\s*=\\s*", 2);
		if(parts.length > 1) {
			String name = parts[1].trim();
			try {
				charset = Charset.forName(name);
			} catch(UnsupportedCharsetException e) {
				LOG.w(".mo defined charset " + name + " is not supported", e);
			}
		}
	}

	private void parsePluralForms() {
		String pluralForms = headers.get("Plural-Forms");
		if(pluralForms == null) {
			return;
		}
		Matcher matcher = PLURAL_FORMS.matcher(pluralForms);
		if(matcher.matches()) {
			try {
				numPlurals = Integer.parseInt(matcher.group(1));
				pluralFormString = matcher.group(2);
				if(pluralFormString.charAt(pluralFormString.length() - 1) == ';') {
					pluralFormString = pluralFormString.substring(0, pluralFormString.length() - 1);
				}
				pluralForm = new Parser(pluralFormString).parse();
			} catch(Exception e) {
				numPlurals = 2;
				pluralFormString = DEFAULT_PLURAL_FORM_STRING;
				LOG.w("Invalid Plural-Forms value: " + pluralForms, e);
			}
		} else {
			LOG.w("Invalid Plural-Forms value: " + pluralForms);
		}
	}

	private static Charset getDefaultCharset() {
		try {
			return Charset.forName("utf-8");
		} catch(UnsupportedCharsetException e) {
			LOG.w("Charset not supported, falling back on default", e);
		}
		return Charset.defaultCharset();
	}
}
