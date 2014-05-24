package com.preferanser.client.application.mvp.player;

import com.gwtplatform.mvp.client.HasUiHandlers;
import com.preferanser.client.application.mvp.TableView;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.Hand;

import java.util.Map;
import java.util.Set;

public interface PlayerView extends TableView, HasUiHandlers<PlayerUiHandlers> {

    void displayHandTricks(Map<Hand, Integer> handTricks);

    void disableCards(Set<Card> cards);

    void displayTurnNavigation(boolean showPrev, boolean showNext);

    void displayResetButton(boolean visible);

    void displaySluffButton(boolean visible);

    void displaySaveDrawingButton(boolean visible);

    void switchTabGame();
}
