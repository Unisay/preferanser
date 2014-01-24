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

package com.preferanser.client.service;

import com.preferanser.client.restygwt.RequestId;
import com.preferanser.shared.domain.entity.Deal;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.*;
import java.util.List;

import static com.preferanser.client.restygwt.RequestIdValue.*;

/**
 * Client rest-client service responsible for deal persistence
 */
@Path("/deal")
@SuppressWarnings("VoidMethodAnnotatedWithGET")
public interface DealService extends RestService {

    /**
     * Persist deal on backend
     *
     * @param deal     deal
     * @param callback contains new deal id as Long
     */
    @POST
    @RequestId(SAVE_DEAL) void persist(Deal deal, MethodCallback<Long> callback);

    @GET
    @RequestId(LOAD_DEALS) void load(MethodCallback<List<Deal>> callback);

    @DELETE
    @Path("/{dealId}")
    @RequestId(DELETE_DEAL) void delete(@PathParam("dealId") Long dealId, MethodCallback<Void> callback);
}
