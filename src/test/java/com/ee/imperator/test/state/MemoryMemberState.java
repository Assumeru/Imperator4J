package com.ee.imperator.test.state;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ee.i18n.Language;
import org.ee.i18n.Language.TextDirection;
import org.ee.i18n.none.DefaultLanguage;
import org.ee.web.request.Request;

import com.ee.imperator.data.MemberState;
import com.ee.imperator.user.Member;

public class MemoryMemberState implements MemberState {
	private final Map<Integer, Member> members;

	public MemoryMemberState() {
		Language language = new DefaultLanguage(Locale.US, TextDirection.LTR);
		members = new HashMap<>();
		members.put(1, new Member(1, "Test User 1", language, false, false, 0, 0, 0));
		members.put(2, new Member(2, "Test User 2", language, false, true, 0, 0, 0));
		members.put(3, new Member(3, "Test User 3", language, false, false, 0, 0, 0));
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public Member getMember(int id) {
		return members.get(id);
	}

	@Override
	public Member getMember(Request request) {
		return null;
	}

	@Override
	public Integer getId(Request request) {
		return null;
	}

	@Override
	public List<Member> getMembers() {
		return new ArrayList<>(members.values());
	}
}
