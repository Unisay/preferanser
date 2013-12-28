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

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.util.cmd.ObjectifyWrapper;

public class Ofy extends ObjectifyWrapper<Ofy, OfyFactory> {

    public Ofy(Objectify base) {
        super(base);
    }

    public <T> LoadType<T> query(Class<T> clazz) {
        return load().type(clazz);
    }

    public <T> T get(Key<T> key) {
        return load().key(key).now();
    }

    public <T> T get(Class<T> clazz, long id) {
        return load().type(clazz).id(id).now();
    }
}