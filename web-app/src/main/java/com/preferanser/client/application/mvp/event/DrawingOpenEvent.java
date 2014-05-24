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

package com.preferanser.client.application.mvp.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.preferanser.shared.domain.Drawing;

public class DrawingOpenEvent extends GwtEvent<DrawingOpenEvent.DrawingOpenHandler> {

    private Drawing drawing;

    @SuppressWarnings("unused")
    protected DrawingOpenEvent() {
        // Possibly for serialization.
    }

    public DrawingOpenEvent(Drawing drawing) {
        this.drawing = drawing;
    }

    public static void fire(HasHandlers source, Drawing deal1) {
        source.fireEvent(new DrawingOpenEvent(deal1));
    }

    public static void fire(HasHandlers source, DrawingOpenEvent eventInstance) {
        source.fireEvent(eventInstance);
    }

    public interface DrawingOpenHandler extends EventHandler {
        public void onDrawingOpenEvent(DrawingOpenEvent event);
    }

    private static final Type<DrawingOpenHandler> TYPE = new Type<DrawingOpenHandler>();

    public static Type<DrawingOpenHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<DrawingOpenHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DrawingOpenHandler handler) {
        handler.onDrawingOpenEvent(this);
    }

    public Drawing getDrawing() {
        return drawing;
    }

}
