package com.preferanser.client.application.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.preferanser.shared.Turn;

public class TurnChangeEvent extends GwtEvent<TurnChangeEvent.TurnChangeEventHandler> {

    private Turn turn;

    protected TurnChangeEvent() {
        // Possibly for serialization.
    }

    public static void fire(HasHandlers source) {
        TurnChangeEvent eventInstance = new TurnChangeEvent();
        source.fireEvent(eventInstance);
    }

    public static void fire(HasHandlers source, TurnChangeEvent eventInstance) {
        source.fireEvent(eventInstance);
    }

    public interface HasTurnChangeEventHandlers extends HasHandlers {
        HandlerRegistration addGlobalHandler(TurnChangeEventHandler handler);
    }

    public interface TurnChangeEventHandler extends EventHandler {
        public void onTurnChange(TurnChangeEvent event);
    }

    private static final Type<TurnChangeEventHandler> TYPE = new Type<TurnChangeEventHandler>();

    public static Type<TurnChangeEventHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<TurnChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TurnChangeEventHandler handler) {
        handler.onTurnChange(this);
    }

    public Turn getTurn() {
        return turn;
    }
}