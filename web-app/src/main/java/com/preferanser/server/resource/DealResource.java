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

import com.google.inject.Inject;
import com.preferanser.server.service.DealService;
import com.preferanser.server.transformer.DealTransformer;
import com.preferanser.shared.domain.Deal;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/deal")
@Produces(MediaType.APPLICATION_JSON) // TODO specify encoding UTF-8
public class DealResource {

    private final DealService dealService;
    private final DealTransformer dealTransformer;

    @Inject
    public DealResource(DealService dealService, DealTransformer dealTransformer) {
        this.dealService = dealService;
        this.dealTransformer = dealTransformer;
    }

    @GET
    public List<Deal> getCurrentUserOrSharedDeals() {
        return dealTransformer.fromEntities(dealService.getCurrentUserOrSharedDeals());
    }

    @GET
    @Path("/{dealId}")
    public Deal getById(@PathParam("dealId") Long dealId) {
        return dealTransformer.fromEntity(dealService.get(dealId));
    }

    @POST
    public Long save(Deal deal) {
        return dealService.save(dealTransformer.toEntity(deal)).getId();
    }

    // TODO: consider using @BeanParam
    @PUT
    @Path("/{dealId}")
    public void update(@PathParam("dealId") Long dealId, Deal deal) {
        deal.setId(dealId);
        dealService.update(dealTransformer.toEntity(deal));
    }

    @DELETE
    @Path("/{dealId}")
    public void delete(@PathParam("dealId") Long dealId) {
        dealService.delete(dealId);
    }

}