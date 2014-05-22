/*
 * Preferanser is a program to simulate and calculate Russian Preferans Card game drawings.
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
import com.preferanser.shared.domain.Drawing;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.*;
import java.util.List;

import static com.preferanser.client.restygwt.RequestIdValue.*;

/**
 * Client rest-client service responsible for drawing persistence
 */
@Path("/drawing")
@SuppressWarnings("VoidMethodAnnotatedWithGET")
public interface DrawingService extends RestService {

    /**
     * Persist drawing on backend
     *
     * @param drawing  drawing
     * @param callback contains new drawing id as Long
     */
    @POST
    @RequestId(SAVE_DRAWING) void save(Drawing drawing, MethodCallback<Long> callback);

    @GET
    @Path("/{dealId}")
    @RequestId(LOAD_DRAWINGS) void load(@PathParam("dealId") Long dealId, MethodCallback<List<Drawing>> callback);

    @GET
    @Path("/{drawingId}")
    @RequestId(GET_DRAWING) void getCurrentUserDrawing(@PathParam("drawingId") Long drawingId, MethodCallback<Drawing> callback);

    @DELETE
    @Path("/{drawingId}")
    @RequestId(DELETE_DRAWING) void delete(@PathParam("drawingId") Long drawingId, MethodCallback<Void> callback);
}
