package com.ee.imperator.data.cache;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.ee.cache.SoftReferenceCache;
import org.ee.web.request.Request;

import com.ee.imperator.ImperatorApplicationContext;
import com.ee.imperator.data.MemberState;
import com.ee.imperator.user.Member;

public class CachedMemberState implements MemberState {
	private final MemberState memberProvider;
	private final Map<Integer, Member> cache;

	public CachedMemberState(MemberState memberProvider, long timeToKeep) {
		this.memberProvider = memberProvider;
		cache = new SoftReferenceCache<>(timeToKeep);
	}

	public CachedMemberState(MemberState memberProvider, ImperatorApplicationContext context) {
		this(memberProvider, context.getLongSetting(CachedMemberState.class, "timeToKeep"));
	}

	@Override
	public Member getMember(int id) {
		Member member = cache.get(id);
		if(member == null) {
			member = memberProvider.getMember(id);
			cache(member);
		}
		return member;
	}

	@Override
	public Member getMember(Request request) {
		return getMember(getId(request));
	}

	private void cache(Member member) {
		cache.put(member.getId(), member);
	}

	@Override
	public int getId(Request request) {
		return memberProvider.getId(request);
	}

	@Override
	public void close() throws IOException {
		memberProvider.close();
	}

	@Override
	public List<Member> getMembers() {
		return memberProvider.getMembers();
	}
}
