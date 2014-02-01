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

package com.preferanser.client.application.mvp;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.preferanser.shared.domain.entity.Deal;

public class DealEvent extends GwtEvent<DealEvent.DealCreatedHandler> {

    private Deal deal;

    @SuppressWarnings("unused")
    protected DealEvent() {
        // Possibly for serialization.
    }

    public DealEvent(Deal deal) {
        this.deal = deal;
    }

    public static void fire(HasHandlers source, Deal deal1) {
        source.fireEvent(new DealEvent(deal1));
    }

    public static void fire(HasHandlers source, DealEvent eventInstance) {
        source.fireEvent(eventInstance);
    }

    public interface HasGameBuiltHandlers extends HasHandlers {
        HandlerRegistration addGlobalHandler(DealCreatedHandler handler);
    }

    public interface DealCreatedHandler extends EventHandler {
        public void onDealEvent(DealEvent event);
    }

    private static final Type<DealCreatedHandler> TYPE = new Type<DealCreatedHandler>();

    public static Type<DealCreatedHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<DealCreatedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DealCreatedHandler handler) {
        handler.onDealEvent(this);
    }

    public Deal getDeal() {
        return deal;
    }

}
