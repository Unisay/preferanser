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

package com.preferanser.client.application.game;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.preferanser.domain.Game;

public class GameBuiltEvent extends GwtEvent<GameBuiltEvent.GameBuiltHandler> {

    private Game game;

    @SuppressWarnings("unused")
    protected GameBuiltEvent() {
        // Possibly for serialization.
    }

    public GameBuiltEvent(Game game) {
        this.game = game;
    }

    public static void fire(HasHandlers source, Game game) {
        source.fireEvent(new GameBuiltEvent(game));
    }

    public static void fire(HasHandlers source, GameBuiltEvent eventInstance) {
        source.fireEvent(eventInstance);
    }

    public interface HasGameBuiltHandlers extends HasHandlers {
        HandlerRegistration addGlobalHandler(GameBuiltHandler handler);
    }

    public interface GameBuiltHandler extends EventHandler {
        public void onGameBuilt(GameBuiltEvent event);
    }

    private static final Type<GameBuiltHandler> TYPE = new Type<GameBuiltHandler>();

    public static Type<GameBuiltHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<GameBuiltHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(GameBuiltHandler handler) {
        handler.onGameBuilt(this);
    }

    public Game getGame() {
        return game;
    }

}
