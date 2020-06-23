/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;

import gov.gtas.constant.GtasSecurityConstants;
import gov.gtas.model.User;

import java.util.List;
import java.util.Set;

import gov.gtas.model.UserGroup;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * The Interface UserService.
 */
public interface UserService {

	/**
	 * Creates the User.
	 *
	 * @param userData
	 *            the user data
	 * @return the user data
	 */
	@PreAuthorize(PRIVILEGE_ADMIN)
	public UserData create(UserData user);

	/**
	 * Delete.
	 *
	 * @param id
	 *            the user id
	 */
	@PreAuthorize(PRIVILEGE_ADMIN)
	public void delete(String id);

	/**
	 * Find all.
	 *
	 * @return the list
	 */
	@PreAuthorize(GtasSecurityConstants.PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	public List<UserDisplayData> findAll();

	/**
	 * Update the user.
	 *
	 * @param userData
	 *            the user data
	 * @return the updated user data
	 */

	public UserData update(UserData user);

	/**
	 * Update the user.
	 *
	 * @param userData
	 *            the user data
	 * @return the updated user data
	 */
	@PreAuthorize(PRIVILEGE_ADMIN)
	public UserData updateByAdmin(UserData user);

	/**
	 * Find by id.
	 *
	 * @param id
	 *            the id
	 * @return the user data
	 */
	public UserData findById(String id);

	/**
	 * Fetch user.
	 *
	 * @param userId
	 *            the user id
	 * @return the user
	 */
	public User fetchUser(final String userId);

	public Set<UserGroup> fetchUserGroups(final String userId);

	public boolean isAdminUser(String userId);

	public boolean treatAsOneDay(String userId);
}
