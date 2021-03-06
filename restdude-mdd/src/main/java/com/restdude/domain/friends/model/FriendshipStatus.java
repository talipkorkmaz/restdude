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
package com.restdude.domain.friends.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public enum FriendshipStatus {
    SENT, DELETE, PENDING, CONFIRMED, BLOCK, BLOCK_INVERSE;


    private static final Logger LOGGER = LoggerFactory.getLogger(FriendshipStatus.class);

    private static Map<FriendshipStatus, Set<FriendshipStatus>> allowedNext = new HashMap<FriendshipStatus, Set<FriendshipStatus>>();
    private static Map<FriendshipStatus, FriendshipStatus> applicableInverse = new HashMap<FriendshipStatus, FriendshipStatus>();


    static {

        // allowed "next" choices per status
        allowedNext.put(null, new HashSet<FriendshipStatus>(Arrays.asList(SENT, BLOCK)));
        allowedNext.put(SENT, new HashSet<FriendshipStatus>(Arrays.asList(DELETE)));
        allowedNext.put(PENDING, new HashSet<FriendshipStatus>(Arrays.asList(CONFIRMED, DELETE, BLOCK)));
        allowedNext.put(CONFIRMED, new HashSet<FriendshipStatus>(Arrays.asList(DELETE, BLOCK)));
        allowedNext.put(BLOCK, new HashSet<FriendshipStatus>(Arrays.asList(DELETE)));

        // applied "inverse" value per selected status
        applicableInverse.put(SENT, PENDING);
        applicableInverse.put(CONFIRMED, CONFIRMED);
        applicableInverse.put(DELETE, DELETE);
        applicableInverse.put(BLOCK, BLOCK_INVERSE);

    }

    /**
     *
     * @param current may be <code>null</code>
     * @return
     */
    public static Set<FriendshipStatus> getAllowedNext(FriendshipStatus current) {
        return allowedNext.get(current);
    }

    /**
     * @param current may be <code>null</code>
     * @param next may be <code>null</code>
     * @return
     */
    public static boolean isAllowedNext(FriendshipStatus current, FriendshipStatus next) {
        boolean allowed = allowedNext.get(current).contains(next);
        LOGGER.debug("isAllowedNext, current: {} ,next: {}, allowed: {}" + allowed, current, next);
        return allowed;
    }


    /**
     *
     * @param current may be <code>null</code>
     * @return
     */
    public static FriendshipStatus getApplicableInverse(FriendshipStatus current) {
        return applicableInverse.get(current);
    }

}