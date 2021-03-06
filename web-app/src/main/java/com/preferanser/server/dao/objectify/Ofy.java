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

package com.preferanser.server.dao.objectify;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.impl.ObjectifyImpl;

public class Ofy extends ObjectifyImpl<Ofy> {

    public Ofy(OfyFactory ofyFactory) {
        super(ofyFactory);
    }

    public <T> LoadType<T> query(Class<T> clazz) {
        return load().type(clazz);
    }

    public <T> Optional<T> get(Key<T> key) {
        return Optional.fromNullable(load().key(key).now());
    }

    public <T> Optional<T> get(Class<T> clazz, long id) {
        Preconditions.checkArgument(id > 0, "Invalid entity id");
        return Optional.fromNullable(load().type(clazz).id(id).now());
    }
}
