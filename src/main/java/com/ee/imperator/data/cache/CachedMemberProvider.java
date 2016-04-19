package com.ee.imperator.data.cache;

import java.io.IOException;

import org.ee.cache.SoftReferenceCache;
import org.ee.web.request.Request;

import com.ee.imperator.Imperator;
import com.ee.imperator.data.MemberProvider;
import com.ee.imperator.user.Member;

public class CachedMemberProvider implements MemberProvider {
	private final MemberProvider memberProvider;
	private final SoftReferenceCache<Integer, Member> cache;

	public CachedMemberProvider(MemberProvider memberProvider, long timeToKeep) {
		this.memberProvider = memberProvider;
		cache = new SoftReferenceCache<>(timeToKeep);
	}

	public CachedMemberProvider(MemberProvider memberProvider) {
		this(memberProvider, Imperator.getConfig().getLong(CachedMemberProvider.class, "timeToKeep"));
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
}
