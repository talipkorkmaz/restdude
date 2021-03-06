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
package com.restdude.domain.cms.model;

import com.restdude.mdd.model.AbstractSystemUuidPersistableModel;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 */
@Entity
@Table(name = "content_folder")
public class Folder extends AbstractSystemUuidPersistableModel {

	private static final long serialVersionUID = -7942906897981646998L;

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Folder)) {
			return false;
		}
		Folder that = (Folder) obj;
        return null == this.getId() ? false : this.getId().equals(this.getId());
    }



}