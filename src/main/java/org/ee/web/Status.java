package org.ee.web;

public class Status {
	public enum Family {
		INFORMATIONAL, SUCCESSFUL, REDIRECTION, CLIENT_ERROR, SERVER_ERROR, UNKNOWN;

		public static Family of(int status) {
			switch(status / 100) {
			case 1: return INFORMATIONAL;
			case 2: return SUCCESSFUL;
			case 3: return REDIRECTION;
			case 4: return CLIENT_ERROR;
			case 5: return SERVER_ERROR;
			default: return UNKNOWN;
			}
		}
	}

	public static final Status OK = new Status(200);
	public static final Status NO_CONTENT = new Status(204);
	public static final Status SEE_OTHER = new Status(303);
	public static final Status BAD_REQUEST = new Status(400);
	public static final Status FORBIDDEN = new Status(403);
	public static final Status NOT_FOUND = new Status(404);
	public static final Status INTERNAL_SERVER_ERROR = new Status(500);

	private final int code;

	public Status(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public Family getFamily() {
		return Family.of(code);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Status) {
			return ((Status) obj).code == code;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 31 * code;
	}
}
