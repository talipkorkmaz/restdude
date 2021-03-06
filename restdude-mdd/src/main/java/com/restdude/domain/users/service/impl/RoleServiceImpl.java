/**
 *
 * Restdude
 * -------------------------------------------------------------------
 *
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.restdude.domain.users.service.impl;

import com.restdude.domain.Roles;
import com.restdude.domain.users.model.Role;
import com.restdude.domain.users.model.User;
import com.restdude.domain.users.repository.RoleRepository;
import com.restdude.domain.users.service.RoleService;
import com.restdude.mdd.service.AbstractPersistableModelServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;

@Slf4j
@Named("roleService")
public class RoleServiceImpl extends AbstractPersistableModelServiceImpl<Role, String, RoleRepository> implements RoleService {


	/**
	 * {@inheritDoc}
	 *
	 * @param systemUser
	 */
	@Override
	protected void initDataOverride(User systemUser) {

		if (this.count() == 0) {
			Role adminRole = new Role(Roles.ROLE_ADMIN, "System Administrator.");
			log.debug("#initRoles, creating");
			adminRole = this.create(adminRole);
			Role siteAdminRole = new Role(Roles.ROLE_SITE_OPERATOR, "Site Operator.");
			siteAdminRole = this.create(siteAdminRole);
			// this is added to users by user service, just creating it
			Role userRole = new Role(Roles.ROLE_USER, "Logged in user");
			userRole = this.create(userRole);
		}
	}

	@Override
	public Role findByIdOrName(String idOrName) {
		Role role = this.repository.findOne(idOrName);
		if (role == null) {
			role = this.repository.findByName(idOrName);
		}
		return role;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteMember(String roleId, String userId) {
		Role role = this.repository.findByIdOrName(roleId);
		User member = userRepository.findOne(userId);
		member.removeRole(role);
		userRepository.save(member);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveMember(String roleId, User user) {
		Role role = this.repository.findByIdOrName(roleId);
        User member = userRepository.findOne(user.getId());
        member.addRole(role);
        userRepository.save(member);
    }


}