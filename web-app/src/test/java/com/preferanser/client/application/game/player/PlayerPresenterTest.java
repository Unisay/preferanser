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

package com.preferanser.client.application.game.player;

import com.google.web.bindery.event.shared.EventBus;
import com.preferanser.client.application.game.GameBuiltEvent;
import com.preferanser.client.place.PlaceManager;
import com.preferanser.shared.domain.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Map;

import static org.mockito.Mockito.*;

public class PlayerPresenterTest {

    private PlayerPresenter presenter;

    @Mock
    private EventBus eventBus;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private PlayerPresenter.PlayerView view;

    @Mock
    private PlayerPresenter.Proxy proxy;

    @Mock
    private Game game;

    @Mock
    private Map<Cardinal, Contract> cardinalContracts;

    @Mock
    private Map<TableLocation, Collection<Card>> cardinalCards;

    @Mock
    private Map<Card, Cardinal> centerCards;

    @Mock
    private Map<Cardinal, Integer> cardinalTricks;

    private Cardinal turn;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new PlayerPresenter(placeManager, eventBus, view, proxy);
        presenter.onGameBuilt(new GameBuiltEvent(game));
        turn = Cardinal.EAST;

        verify(view).setUiHandlers(presenter);
    }

    @Test
    public void testChangeCardLocation_NotToCenter() throws Exception {
        when(game.getCardinalCards()).thenReturn(cardinalCards);
        when(game.getCenterCards()).thenReturn(centerCards);

        presenter.changeCardLocation(Card.CLUB_ACE, TableLocation.WEST, TableLocation.EAST);

        verify(game).getCardinalCards();
        verify(game).getCenterCards();
        verify(view).displayTableCards(cardinalCards, centerCards);
        verifyNoMoreInteractions(view);
        verifyNoMoreInteractions(game);
    }

    @Test
    public void testChangeCardLocation() throws Exception {
        when(game.getTurn()).thenReturn(turn);
        when(game.getCardinalContracts()).thenReturn(cardinalContracts);
        when(game.getCardinalCards()).thenReturn(cardinalCards);
        when(game.getCenterCards()).thenReturn(centerCards);
        when(game.getCardinalTricks()).thenReturn(cardinalTricks);

        presenter.changeCardLocation(Card.CLUB_ACE, TableLocation.EAST, TableLocation.CENTER);

        verify(view).displayTurn(turn);
        verify(view).displayContracts(cardinalContracts);
        verify(view).displayTableCards(cardinalCards, centerCards);
        verify(view).displayCardinalTricks(cardinalTricks);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testChangeCardLocation_CompleteTrick() throws Exception {
        when(game.isTrickComplete()).thenReturn(true);
        when(game.getTurn()).thenReturn(turn);
        when(game.getCardinalContracts()).thenReturn(cardinalContracts);
        when(game.getCardinalCards()).thenReturn(cardinalCards);
        when(game.getCenterCards()).thenReturn(centerCards);
        when(game.getCardinalTricks()).thenReturn(cardinalTricks);

        presenter.changeCardLocation(Card.CLUB_ACE, TableLocation.EAST, TableLocation.CENTER);

        verify(view).displayTurn(turn);
        verify(view).displayContracts(cardinalContracts);
        verify(view).displayTableCards(cardinalCards, centerCards);
        verify(view).displayCardinalTricks(cardinalTricks);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testSluff() throws Exception {
    }

    @Test
    public void testSwitchToEditor() throws Exception {
    }

    @Test
    public void testReset() throws Exception {
    }

}
