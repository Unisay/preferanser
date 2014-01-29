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
import com.preferanser.server.exception.NotAuthorizedUserException;
import com.preferanser.server.service.AuthenticationService;
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.entity.User;
import com.preferanser.shared.util.Clock;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/deal")
@Produces(MediaType.APPLICATION_JSON) // TODO specify encoding UTF-8
public class DealResource {

    private final AuthenticationService authenticationService;
    private final DealDao dealDao;

    @Inject
    public DealResource(AuthenticationService authenticationService, DealDao dealDao) {
        this.authenticationService = authenticationService;
        this.dealDao = dealDao;
    }

    @GET
    public List<Deal> getAllSharedDeals() {
        return dealDao.getAllSharedDeals();
    }

    @GET
    @Path("/{dealId}")
    public Deal getById(@PathParam("dealId") Long dealId) {
        return dealDao.get(dealId);
    }

    @POST
    public Long save(Deal deal) {
        Optional<User> currentUserOptional = authenticationService.getCurrentUser();

        if (!currentUserOptional.isPresent())
            throw new NoAuthenticatedUserException();

        if (deal.isShared() && !currentUserOptional.get().getAdmin())
            throw new NotAuthorizedUserException("Only admins can create shared deals");

        deal.setId(null);
        deal.setUserId(currentUserOptional.get().getGoogleId());
        deal.setCreated(Clock.getNow());
        Deal savedDeal = dealDao.save(deal);

        assert savedDeal != null : "dealDao.save(" + deal + ") returned null";
        return savedDeal.getId();
    }

    @DELETE
    @Path("/{dealId}")
    public void delete(@PathParam("dealId") Long dealId) {
        Optional<User> currentUserOptional = authenticationService.getCurrentUser();

        if (!currentUserOptional.isPresent())
            throw new NoAuthenticatedUserException(); // TODO force HTTP status

        Deal deal = dealDao.get(dealId);
        if (!deal.getUserId().equals(currentUserOptional.get().getGoogleId()))
            throw new NotAuthorizedUserException();

        dealDao.delete(deal);
    }

}