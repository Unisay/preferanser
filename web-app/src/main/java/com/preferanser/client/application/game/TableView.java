package com.preferanser.client.application.game;

import com.gwtplatform.mvp.client.View;
import com.preferanser.domain.Card;
import com.preferanser.domain.Cardinal;
import com.preferanser.domain.Contract;
import com.preferanser.domain.TableLocation;

import java.util.Collection;
import java.util.Map;

public interface TableView extends View {

    void displayTurn(Cardinal turn);
    void displayContracts(Map<Cardinal, Contract> cardinalContracts);
    void displayCardinalTricks(Map<Cardinal, Integer> cardinalTricks);
    void displayTableCards(Map<TableLocation, Collection<Card>> tableCards, Map<Card, Cardinal> centerCards);

}
