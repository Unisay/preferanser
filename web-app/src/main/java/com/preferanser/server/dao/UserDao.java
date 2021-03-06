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


import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.preferanser.server.dao.objectify.OfyFactory;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.server.exception.NotFoundException;

import javax.validation.Validator;

public class UserDao extends BaseDao<UserEntity> {

    @Inject
    public UserDao(OfyFactory ofyFactory, Validator validator) {
        super(UserEntity.class, ofyFactory, validator);
    }

    public Optional<UserEntity> findById(Long id) {
        return find(Key.create(UserEntity.class, id));
    }

    public UserEntity getById(Long id) {
        Optional<UserEntity> userEntityOptional = find(Key.create(UserEntity.class, id));
        if (!userEntityOptional.isPresent()) {
            throw new NotFoundException(UserEntity.class, id);
        }
        return userEntityOptional.get();
    }

    public Optional<UserEntity> findByGoogleId(String googleId) {
        return Optional.fromNullable(query().filter("googleId", googleId).limit(1).first().now());
    }

}
