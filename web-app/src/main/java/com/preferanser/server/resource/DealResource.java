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

package com.preferanser.server.resource;

/**
 * REST resource responsible for an authorization
 */

import com.google.inject.Inject;
import com.preferanser.server.dao.DealDao;
import com.preferanser.shared.domain.entity.Deal;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/deal")
public class DealResource {

    private final DealDao dealDao;

    @Inject
    public DealResource(DealDao dealDao) {
        this.dealDao = dealDao;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void save(Deal deal) {
        // TODO: add user id
        dealDao.save(deal);
    }
}