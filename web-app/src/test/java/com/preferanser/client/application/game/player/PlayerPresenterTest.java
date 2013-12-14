package com.preferanser.client.application.game.player;

import com.google.web.bindery.event.shared.EventBus;
import com.preferanser.domain.*;
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
        presenter = new PlayerPresenter(eventBus, view, proxy);
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

        verify(view).hideTurn();
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
