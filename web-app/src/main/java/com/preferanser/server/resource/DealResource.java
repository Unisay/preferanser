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

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.preferanser.server.dao.DealDao;
import com.preferanser.server.exception.NoAuthenticatedUserException;
import com.preferanser.shared.domain.entity.Deal;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/deal")
@Produces(MediaType.APPLICATION_JSON)
public class DealResource {

    private final AuthenticationService authenticationService;
    private final DealDao dealDao;

    @Inject
    public DealResource(AuthenticationService authenticationService, DealDao dealDao) {
        this.authenticationService = authenticationService;
        this.dealDao = dealDao;
    }

    @GET
    public List<Deal> load() {
        return dealDao.getAllDescDateCreated();
    }

    @POST
    public void save(Deal deal) {
        Optional<String> currentUserId = authenticationService.getCurrentUserId();
        if (!currentUserId.isPresent())
            throw new NoAuthenticatedUserException();
        deal.setUserId(currentUserId.get());
        dealDao.save(deal);
    }

    @DELETE
    @Path("/{dealId}")
    public void delete(@PathParam("dealId") Long dealId) {
        Optional<String> currentUserId = authenticationService.getCurrentUserId();
        if (!currentUserId.isPresent())
            throw new NoAuthenticatedUserException(); // TODO replace by standard JAX-RS exception
        Deal deal = dealDao.get(dealId);
        if (! deal.getUserId().equals(currentUserId.get()))
            throw new NoAuthenticatedUserException(); // TODO replace by standard JAX-RS exception
        dealDao.delete(deal);
    }

}