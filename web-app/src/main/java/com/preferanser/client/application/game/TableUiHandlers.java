package com.preferanser.client.application.game;

import com.gwtplatform.mvp.client.UiHandlers;
import com.preferanser.domain.Card;
import com.preferanser.domain.TableLocation;

public interface TableUiHandlers extends UiHandlers {

    void reset();

    /**
     * User changed card location
     *
     * @param card        card
     * @param oldLocation old location
     * @param newLocation new location
     */
    void changeCardLocation(Card card, TableLocation oldLocation, TableLocation newLocation);
}
