package com.ee.imperator.test.state;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ee.web.request.Request;

import com.ee.imperator.data.MemberState;
import com.ee.imperator.user.Member;

public class MemoryMemberState implements MemberState {
	private final Map<Integer, Member> members;

	public MemoryMemberState() {
		members = new HashMap<>();
		members.put(1, new Member(1, "Test User 1", null, false, false, 0, 0, 0));
		members.put(2, new Member(2, "Test User 2", null, false, true, 0, 0, 0));
		members.put(3, new Member(3, "Test User 3", null, false, false, 0, 0, 0));
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
	public void addWin(Member member, int points) {
		member.setWins(member.getWins() + 1);
		member.setScore(member.getScore() + points);
	}

	@Override
	public void addLoss(Member member) {
		member.setLosses(member.getLosses() + 1);
		member.setScore(member.getScore() - 1);
	}

	@Override
	public List<Member> getMembers() {
		return new ArrayList<>(members.values());
	}
}