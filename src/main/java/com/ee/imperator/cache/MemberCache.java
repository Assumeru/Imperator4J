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
		Integer id = getId(request);
		if(id == null) {
			return getDefaultMember();
		}
		return getMember(id);
	}

	private Integer getId(Request reques) {
		//TODO
		return null;
	}

	public Member getMember(int id) {
		Member member = cache.get(id);
		if(member != null) {
			return member;
		}
		//TODO
		return getDefaultMember();
	}

	private Member getDefaultMember() {
		Member member = cache.get(null);
		if(member == null) {
			member = new Member();
			cache.put(null, member);
		}
		return member;
	}

	public void clear() {
		cache.clear();
	}
}
