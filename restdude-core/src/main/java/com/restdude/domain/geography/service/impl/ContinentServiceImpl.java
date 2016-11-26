/**
 * calipso-hub-framework - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.restdude.domain.geography.service.impl;

import com.restdude.domain.base.service.AbstractModelServiceImpl;
import com.restdude.domain.geography.model.Continent;
import com.restdude.domain.geography.repository.ContinentRepository;
import com.restdude.domain.geography.service.ContinentService;

import javax.inject.Named;


@Named("continentService")
public class ContinentServiceImpl extends AbstractModelServiceImpl<Continent, String, ContinentRepository> implements ContinentService {

}