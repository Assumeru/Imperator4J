package org.ee.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriTemplate {
	private enum State {
		URI, NAME, REGEX
	}
	private static final Pattern NAME = Pattern.compile("\\w[\\w\\.-]*");
	private final List<String> params;
	private final Pattern pattern;

	public UriTemplate(String uri) {
		ArrayList<String> params = new ArrayList<>();
		StringBuilder pattern = new StringBuilder();
		StringBuilder piece = new StringBuilder();
		State state = State.URI;
		int level = 0;
		for(int i = 0; i < uri.length(); i++) {
			char c = uri.charAt(i);
			if(c == '{') {
				level++;
				if(level == 1) {
					state = State.NAME;
					pattern.append(Pattern.quote(piece.toString()));
					piece.setLength(0);
					continue;
				}
			} else if(c == '}' && level > 0) {
				level--;
				if(level == 0) {
					if(state == State.NAME) {
						params.add(validateName(piece.toString()));
						pattern.append("([^/]+)");
					} else {
						pattern.append('(').append(piece.toString().trim()).append(')');
					}
					state = State.URI;
					piece.setLength(0);
					continue;
				}
			} else if(c == ':' && state == State.NAME) {
				state = State.REGEX;
				params.add(validateName(piece.toString()));
				piece.setLength(0);
				continue;
			}
			piece.append(c);
		}
		if(state == State.URI) {
			pattern.append(Pattern.quote(piece.toString()));
		}
		params.trimToSize();
		this.params = Collections.unmodifiableList(params);
		this.pattern = Pattern.compile(pattern.toString());
	}

	public List<String> getParams() {
		return params;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public boolean matches(String input) {
		return pattern.matcher(input).matches();
	}

	public Map<String, String> match(String input) {
		Map<String, String> matches = new LinkedHashMap<>();
		Matcher matcher = pattern.matcher(input);
		if(matcher.find()) {
			int group = 1;
			for(String name : params) {
				String match = matcher.group(group++);
				if(match == null) {
					throw new IllegalArgumentException("No match found for " + name);
				}
				matches.put(name, match);
			}
		} else {
			throw new IllegalArgumentException(input + " does not match");
		}
		return matches;
	}

	private String validateName(String name) {
		name = name.trim();
		if(!NAME.matcher(name).matches()) {
			throw new IllegalArgumentException("Invalid name " + name);
		}
		return name;
	}
}
