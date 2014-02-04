/*
 * Preferanser is a program to simulate and calculate Russian Preferans Card game deals.
 *
 *     Copyright (C) 2013  Yuriy Lazarev <Yuriy.Lazarev@gmail.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see [http://www.gnu.org/licenses/].
 */

package com.preferanser.server.dao;


import com.google.inject.Inject;
import com.preferanser.server.dao.objectify.OfyFactory;
import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.entity.UserEntity;

import javax.validation.Validator;
import java.util.List;

public class DealDao extends BaseDao<DealEntity> {

    @Inject
    public DealDao(OfyFactory ofyFactory, Validator validator) {
        super(DealEntity.class, ofyFactory, validator);
    }

    public List<DealEntity> getAllDescDateCreated() {
        return query().order("-created").list();
    }

    public List<DealEntity> getSharedDeals() {
        return query().filter("shared", true).order("-created").list();
    }

    public List<DealEntity> getUserDeals(UserEntity user) {
        return query().ancestor(user).order("-created").list();
    }

}
