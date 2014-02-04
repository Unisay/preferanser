package com.preferanser.client.application.mvp.editor;

import com.google.common.base.Optional;
import com.gwtplatform.mvp.client.UiHandlers;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.TableLocation;

public interface TableUiHandlers extends UiHandlers {

    /**
     * UserEntity changed card location
     *
     * @param card        card
     * @param newLocation new location is optional
     */
    void changeCardLocation(Card card, Optional<TableLocation> newLocation);
}
