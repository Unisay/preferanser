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

package com.preferanser.client.application.mvp.editor;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.i18n.PreferanserMessages;
import com.preferanser.client.application.mvp.editor.dialog.EditorDialogs;
import com.preferanser.client.service.DealService;
import com.preferanser.shared.domain.*;
import com.preferanser.shared.dto.CurrentUserDto;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;

public class EditorPresenterTest {

    private EditorPresenter presenter;

    @Mock
    private EventBus eventBus;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private EditorPresenter.EditorView view;

    @Mock
    private EditorPresenter.Proxy proxy;

    @Mock
    private EditorDialogs editorDialogs;

    @Mock
    private DealService dealService;

    @Mock
    private GameBuilder gameBuilder;

    @Mock
    private Game game;

    @Mock
    private Map<Hand, Contract> handContracts;

    @Mock
    private Map<Hand, Set<Card>> handCards;

    @Mock
    private Map<Card, Hand> centerCards;

    @Mock
    private Map<Hand, Integer> handTricks;

    @Mock
    private PreferanserMessages preferanserMessages;

    @Mock
    private PreferanserConstants preferanserConstants;

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
        when(gameBuilder.build()).thenReturn(game);
        when(gameBuilder.getFirstTurn()).thenReturn(turn);
        when(gameBuilder.getWidow()).thenReturn(widow);
        when(gameBuilder.getFirstTurn()).thenReturn(turn);
        when(gameBuilder.getHandContracts()).thenReturn(handContracts);
        when(gameBuilder.getHandCards()).thenReturn(handCards);
        when(gameBuilder.getCenterCards()).thenReturn(centerCards);

        presenter = new EditorPresenter(
            placeManager,
            eventBus,
            view,
            proxy,
            gameBuilder,
            dealService,
            preferanserMessages,
            preferanserConstants,
            editorDialogs,
            currentUserDto);

        verify(view).setUiHandlers(presenter);
        verify(view).displayAuthInfo(loggedInAs);
    }

    @Test
    public void testChangeCardLocation_EqualLocations() throws Exception {
        when(gameBuilder.moveCard(Card.CLUB_ACE, TableLocation.EAST, TableLocation.WEST)).thenReturn(true);

        presenter.changeCardLocation(Card.CLUB_ACE, TableLocation.EAST, TableLocation.WEST);

        verify(view).displayTurn(turn);
        verify(view).displayContracts(handContracts);
        verify(view).displayCards(handCards, centerCards, widow);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testOnReveal() throws Exception {

    }

    @Test
    public void testReset() throws Exception {

    }

    @Test
    public void testSluff() throws Exception {

    }

    @Test
    public void testChooseContract() throws Exception {

    }

    @Test
    public void testChooseTurn() throws Exception {

    }

    @Test
    public void testSetHandContract() throws Exception {

    }

    @Test
    public void testSetPlayMode() throws Exception {

    }

    @Test
    public void testSetEditMode() throws Exception {

    }
}
