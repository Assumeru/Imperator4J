package com.ee.imperator.data.dummy;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import org.ee.web.request.Request;

import com.ee.imperator.Imperator;
import com.ee.imperator.data.MemberProvider;
import com.ee.imperator.data.cache.CachedMemberProvider;
import com.ee.imperator.user.Member;

public class DummyMemberProvider extends CachedMemberProvider {
	public DummyMemberProvider() {
		super(new MemberProvider() {
			@Override
			public void close() throws IOException {
			}

			@Override
			public Member getMember(Request request) {
				return getMember(getId(request));
			}

			@Override
			public Member getMember(int id) {
				return new Member(id, "Dummy user #" + id, Imperator.getLanguageProvider().getLanguage(Locale.US), true, 0, 0, 0);
			}

			@Override
			public Integer getId(Request request) {
				return Objects.hashCode(request.getRequest().getHeader("User-Agent"));
			}
		});
	}
}
