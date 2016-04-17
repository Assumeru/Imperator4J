package com.ee.imperator.cache;

import org.ee.cache.SoftReferenceCache;
import org.ee.web.request.Request;

import com.ee.imperator.user.Member;

public class MemberCache {
	private SoftReferenceCache<Integer, Member> cache;

	public MemberCache(long timeToKeep) {
		cache = new SoftReferenceCache<>(timeToKeep);
	}

	public Member getMember(Request request) {
		//TODO
		return new Member();
	}

	public Member getMember(int id) {
		Member member = cache.get(id);
		if(member != null) {
			return member;
		}
		//TODO
		return new Member();
	}

	public void clear() {
		cache.clear();
	}
}
