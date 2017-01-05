/**
 *
 * Restdude
 * -------------------------------------------------------------------
 * Module restdude-core, https://manosbatsis.github.io/restdude/restdude-core
 *
 * Full stack, high level framework for horizontal, model-driven application hackers.
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
package com.restdude.auth.acl.service.impl;

import com.restdude.auth.acl.model.AclEntry;
import com.restdude.auth.acl.repository.AclEntryRepository;
import com.restdude.auth.acl.service.AclEntryService;
import com.restdude.domain.base.service.impl.AbstractAclAwareServiceImpl;

import javax.inject.Named;

@Named("aclEntryService")
public class AclEntryServiceImpl extends
		AbstractAclAwareServiceImpl<AclEntry, Long, AclEntryRepository> implements
		AclEntryService {

}

