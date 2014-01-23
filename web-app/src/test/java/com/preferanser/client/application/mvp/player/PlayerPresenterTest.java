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

package com.preferanser.client.application.mvp.player;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.preferanser.client.application.i18n.PreferanserMessages;
import com.preferanser.client.application.mvp.GameBuiltEvent;
import com.preferanser.shared.domain.*;
import com.preferanser.shared.dto.CurrentUserDto;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

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
    private PreferanserMessages preferanserMessages;

    @Mock
    private Map<Hand, Contract> handContracts;

    @Mock
    private Map<Hand, Set<Card>> handCards;

    @Mock
    private Map<Card, Hand> centerCards;

    @Mock
    private Map<Hand, Integer> handTricks;

    private Hand turn;
    private Widow widow;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        turn = Hand.EAST;
        widow = new Widow();

        CurrentUserDto currentUserDto = new CurrentUserDto();
        currentUserDto.nickname = "nickname";
        String loggedInAs = "logged in as nickname";

        when(preferanserMessages.loggedInAs(currentUserDto.nickname)).thenReturn(loggedInAs);
        when(game.getTurn()).thenReturn(turn);
        when(game.getWidow()).thenReturn(widow);
        when(game.getHandContracts()).thenReturn(handContracts);
        when(game.getHandCards()).thenReturn(handCards);
        when(game.getCenterCards()).thenReturn(centerCards);
        when(game.getHandTrickCounts()).thenReturn(handTricks);
        when(game.isTrickClosed()).thenReturn(true);
        when(game.hasUndoTurns()).thenReturn(true);
        when(game.hasRedoTurns()).thenReturn(false);

        presenter = new PlayerPresenter(placeManager, eventBus, view, proxy, preferanserMessages, currentUserDto);
        presenter.onGameBuilt(new GameBuiltEvent(game));

        verify(view).setUiHandlers(presenter);
        verify(view).displayAuthInfo(loggedInAs);
    }

    @Test
    public void testMakeTurn() throws Exception {
        presenter.makeTurn(Card.CLUB_ACE);

        verify(view).displayTurn(turn);
        verify(view).displayContracts(handContracts);
        verify(view).displayCards(handCards, centerCards, widow);
        verify(view).disableCards(Collections.<Card>emptySet());
        verify(view).displayHandTricks(handTricks);
        verify(view).displayTurnNavigation(true, false);
        verify(view).displaySluffButton(true);
        verifyNoMoreInteractions(view);
    }

}
