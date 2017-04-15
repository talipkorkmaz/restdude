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
package com.restdude.domain.error.model;

import com.restdude.domain.cases.model.AbstractCaseCommentModel;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Comments for discussing errors
 */
@Entity
@Table(name = "error_comment")
public class ErrorComment extends AbstractCaseCommentModel<BaseError, ErrorComment>   {


    public ErrorComment() {
    }

    public ErrorComment(BaseError subject, String content) {
        this.setSubject(subject);
        this.setContent(content);
    }
}
