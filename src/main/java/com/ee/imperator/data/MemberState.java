package com.ee.imperator.data;

import java.io.Closeable;
import java.util.List;

import org.ee.web.request.Request;

import com.ee.imperator.user.Member;

public interface MemberState extends Closeable {
	Member getMember(int id);

	Member getMember(Request request);

	Integer getId(Request request);

	List<Member> getMembers();
}
