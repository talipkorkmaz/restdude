/*
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
define(['jquery', 'underscore', 'bloodhound', 'typeahead', "lib/restdudelib/util", "lib/restdudelib/form",
        "lib/restdudelib/uifield", "lib/restdudelib/backgrid", "lib/restdudelib/view", 'handlebars', "lib/restdudelib/models/Model"],
    function ($, _, Bloodhoud, Typeahead, Restdude, RestdudeForm, RestdudeField, RestdudeGrid, RestdudeView, Handlebars) {

        Restdude.model.RoleModel = Restdude.Model.extend(
            /** @lends Restdude.model.RoleModel.prototype */
            {
                toString: function () {
                    return this.get("name");
                }
            }, {
                // static members
                labelIcon: "fa fa-users fa-fw",
                pathFragment: "roles",
                typeName: "Restdude.model.RoleModel",
                menuConfig: {
                    rolesIncluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR"],
                    rolesExcluded: null,
                },


                fields: {
                    name: {
                        fieldType: "String",
                        backgrid: {
                            cell: Restdude.components.backgrid.ViewRowCell,
                        }
                    },
                    description: {
                        fieldType: "String",
                    },
                    edit: {
                        fieldType: "Edit",
                    },
                },
            });

    });