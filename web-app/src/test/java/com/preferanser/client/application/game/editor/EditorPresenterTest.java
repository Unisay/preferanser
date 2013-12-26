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

package com.preferanser.client.application.game.editor;

import com.google.web.bindery.event.shared.EventBus;
import com.preferanser.client.application.game.editor.dialog.contract.ContractDialogPresenter;
import com.preferanser.client.application.game.editor.dialog.validation.ValidationDialogPresenter;
import com.preferanser.client.place.PlaceManager;
import com.preferanser.client.request.PreferanserRequestFactory;
import com.preferanser.domain.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Map;

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
    private ContractDialogPresenter contractDialog;

    @Mock
    private ValidationDialogPresenter validationDialog;

    @Mock
    private GameBuilder gameBuilder;

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

    @Mock
    private PreferanserRequestFactory requestFactory;

    private Cardinal turn;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(gameBuilder.build()).thenReturn(game);
        presenter = new EditorPresenter(placeManager, eventBus, view, proxy, gameBuilder, contractDialog, validationDialog,requestFactory);
        turn = Cardinal.EAST;

        verify(view).setUiHandlers(presenter);
    }

    @Test
    public void testChangeCardLocation_EqualLocations() throws Exception {
        when(gameBuilder.getTableCards()).thenReturn(cardinalCards);
        when(gameBuilder.getCenterCards()).thenReturn(centerCards);

        presenter.changeCardLocation(Card.CLUB_ACE, TableLocation.EAST, TableLocation.EAST);

        verify(gameBuilder).getTableCards();
        verify(gameBuilder).getCenterCards();
        verify(view).displayTableCards(cardinalCards, centerCards);

        verifyNoMoreInteractions(view);
        verifyNoMoreInteractions(game);
        verifyNoMoreInteractions(gameBuilder);
    }

    @Test
    public void testChangeCardLocation_EditMode() throws Exception {
        when(gameBuilder.getFirstTurn()).thenReturn(turn);
        when(gameBuilder.getCardinalContracts()).thenReturn(cardinalContracts);
        when(gameBuilder.getTableCards()).thenReturn(cardinalCards);
        when(gameBuilder.getCenterCards()).thenReturn(centerCards);

        presenter.changeCardLocation(Card.CLUB_ACE, TableLocation.EAST, TableLocation.WEST);

        verify(view).displayTurn(turn);
        verify(view).displayContracts(cardinalContracts);
        verify(view).displayTableCards(cardinalCards, centerCards);
        verify(view).hideCardinalTricks();
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
    public void testSetCardinalContract() throws Exception {

    }

    @Test
    public void testSetPlayMode() throws Exception {

    }

    @Test
    public void testSetEditMode() throws Exception {

    }
}
