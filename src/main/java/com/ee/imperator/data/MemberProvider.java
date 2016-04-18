package com.ee.imperator.data;

import org.ee.web.request.Request;

import com.ee.imperator.user.Member;

public interface MemberProvider {
	Member getMember(int id);

	Member getMember(Request request);
}
