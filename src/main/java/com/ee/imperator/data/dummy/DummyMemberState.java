package com.ee.imperator.data.dummy;

import java.util.Locale;
import java.util.Objects;

import org.ee.web.request.Request;

import com.ee.imperator.Imperator;
import com.ee.imperator.data.cache.CachedMemberState;
import com.ee.imperator.data.db.SqlMemberState;
import com.ee.imperator.data.db.dbcp.DBCPProvider;
import com.ee.imperator.user.Member;

public class DummyMemberState extends CachedMemberState {
	public DummyMemberState() {
		super(new SqlMemberState(DBCPProvider.getDataSource()) {
			@Override
			public Member getMember(Request request) {
				return getMember(getId(request));
			}

			@Override
			public Member getMember(int id) {
				Member member = super.getMember(id);
				return new Member(id, "Dummy user #" + id, Imperator.getLanguageProvider().getLanguage(Locale.US), false, false, member.getScore(), member.getWins(), member.getLosses());
			}

			@Override
			public Integer getId(Request request) {
				return Objects.hashCode(request.getRequest().getHeader("User-Agent"));
			}
		});
	}
}
