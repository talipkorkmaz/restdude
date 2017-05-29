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
package com.restdude.domain.cases.listener;

import com.restdude.domain.cases.model.Space;
import com.restdude.domain.cases.model.enums.ContextVisibilityType;
import com.restdude.domain.cases.service.SpaceService;
import com.restdude.domain.users.event.UserCreatedEvent;
import com.restdude.domain.users.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Creates a "personal" {@link Space} for a newly persisted {@link User}
 */
@Slf4j
@Component
public class UserCreatedEventListener  implements ApplicationListener<UserCreatedEvent> {

    @Autowired
    SpaceService spaceService;

    @Override
    public void onApplicationEvent(UserCreatedEvent event) {

        User user = event.getModel();
        String name = user.getUsername();

        String title = user.getName();
        if(StringUtils.isBlank(title)){
            title = name;
        }

        String description = user.getDescription();
        if(StringUtils.isBlank(description)){
            description = "Space for " + name;
        }

        Space space = this.spaceService.create(new Space.Builder()
                .owner(user)
                .name(name)
                .title(title)
                .description(description)
                .visibility(ContextVisibilityType.CLOSED)
                .build());
        log.debug("Created user space: ", space);
    }
}
