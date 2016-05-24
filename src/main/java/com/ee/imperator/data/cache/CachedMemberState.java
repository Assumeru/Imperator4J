package com.ee.imperator.data.cache;

import java.io.IOException;
import java.util.List;

import org.ee.cache.SoftReferenceCache;
import org.ee.web.request.Request;

import com.ee.imperator.Imperator;
import com.ee.imperator.data.MemberState;
import com.ee.imperator.user.Member;

public class CachedMemberState implements MemberState {
	private final MemberState memberProvider;
	private final SoftReferenceCache<Integer, Member> cache;

	public CachedMemberState(MemberState memberProvider, long timeToKeep) {
		this.memberProvider = memberProvider;
		cache = new SoftReferenceCache<>(timeToKeep);
	}

	public CachedMemberState(MemberState memberProvider) {
		this(memberProvider, Imperator.getConfig().getLong(CachedMemberState.class, "timeToKeep"));
	}

	@Override
	public Member getMember(int id) {
		Member member = cache.get(id);
		if(member == null) {
			member = memberProvider.getMember(id);
			if(member == null) {
				return null;
			}
			cache(member);
		}
		return member;
	}

	@Override
	public Member getMember(Request request) {
		Integer id = getId(request);
		if(id == null) {
			return null;
		}
		return getMember(id);
	}

	private void cache(Member member) {
		cache.put(member.getId(), member);
	}

	@Override
	public Integer getId(Request request) {
		return memberProvider.getId(request);
	}

	@Override
	public void close() throws IOException {
		memberProvider.close();
	}

	@Override
	public void addWin(Member member, int points) {
		memberProvider.addWin(member, points);
	}

	@Override
	public void addLoss(Member member) {
		memberProvider.addLoss(member);
	}

	@Override
	public List<Member> getMembers() {
		return memberProvider.getMembers();
	}
}
