package com.ee.imperator.data;

import java.io.Closeable;
import java.util.List;

import org.ee.web.request.Request;

import com.ee.imperator.user.Member;

/**
 * This interface provides a way to retrieve members.
 * <p>
 * The member id {@code 0} is reserved for guests.
 */
public interface MemberState extends Closeable {
	/**
	 * Gets a member by id.
	 * 
	 * @param id The id of the member to get
	 * @return The member or a guest if no member could be found
	 */
	Member getMember(int id);

	/**
	 * Gets the member associated with a request.
	 * 
	 * @param request The request to get a member for
	 * @return The member making the request or a guest if no member could be found
	 */
	Member getMember(Request request);

	/**
	 * Checks if a member is logged in.
	 * 
	 * @param request The request to get an id for
	 * @return The id of the member making the request
	 */
	int getId(Request request);

	/**
	 * @return A list of all members ordered by score.
	 */
	List<Member> getMembers();
}
