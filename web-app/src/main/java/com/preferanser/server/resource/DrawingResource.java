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

package com.preferanser.server.resource;

import com.google.inject.Inject;
import com.preferanser.server.service.DrawingService;
import com.preferanser.server.transformer.DrawingTransformer;
import com.preferanser.shared.domain.Drawing;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/drawing")
@Produces(MediaType.APPLICATION_JSON)
public class DrawingResource {

    private final DrawingService drawingService;
    private final DrawingTransformer drawingTransformer;

    @Inject
    public DrawingResource(DrawingService drawingService, DrawingTransformer drawingTransformer) {
        this.drawingService = drawingService;
        this.drawingTransformer = drawingTransformer;
    }

    @GET
    @Path("/{dealId}/{drawingId}")
    public Drawing getById(@PathParam("dealId") Long dealId, @PathParam("drawingId") Long drawingId) {
        return drawingTransformer.fromEntity(drawingService.get(dealId, drawingId));
    }

    @POST
    public Long save(Drawing drawing) {
        return drawingService.save(drawingTransformer.toEntity(drawing)).getId();
    }

    // TODO: consider using @BeanParam
    @PUT
    @Path("/{drawingId}")
    public void update(@PathParam("drawingId") Long drawingId, Drawing drawing) {
        throw new UnsupportedOperationException("Not implemented");
//        drawing.setId(drawingId);
//        drawingService.update(drawingTransformer.toEntity(drawing));
    }

    @DELETE
    @Path("/{drawingId}")
    public void delete(@PathParam("drawingId") Long drawingId) {
        throw new UnsupportedOperationException("Not implemented");
//        drawingService.delete(drawingId);
    }

}