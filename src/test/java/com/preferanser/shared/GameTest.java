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

package com.preferanser.shared;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

/**
 * Unit test for the Game
 */
public class GameTest {

    private Game game;
    private Map<TableLocation, Collection<Card>> expectedTableCards;
    private Map<Cardinal, Contract> expectedCardinalContracts;
    private Map<Cardinal, Integer> expectedCardinalTricks;

    @Before
    public void setUp() throws Exception {
        game = new Game();

        expectedTableCards = Maps.newHashMap();
        expectedTableCards.put(TableLocation.NORTH, Collections.<Card>emptyList());
        expectedTableCards.put(TableLocation.EAST, Collections.<Card>emptyList());
        expectedTableCards.put(TableLocation.SOUTH, Collections.<Card>emptyList());
        expectedTableCards.put(TableLocation.WEST, Collections.<Card>emptyList());
        expectedTableCards.put(TableLocation.CENTER, Collections.<Card>emptyList());

        expectedCardinalContracts = Maps.newHashMap();

        expectedCardinalTricks = Maps.newHashMap();
        expectedCardinalTricks.put(Cardinal.NORTH, 0);
        expectedCardinalTricks.put(Cardinal.EAST, 0);
        expectedCardinalTricks.put(Cardinal.WEST, 0);
        expectedCardinalTricks.put(Cardinal.SOUTH, 0);
    }

    @Test
    public void testPutCards_Empty() throws Exception {
        game.putCards(Cardinal.NORTH);
        game.putCards(Cardinal.EAST);
        game.putCards(Cardinal.SOUTH);
        game.putCards(Cardinal.WEST);
        assertReflectionEquals(expectedTableCards, game.getTableCards());
    }

    @Test
    public void testPutCards_Some() throws Exception {
        expectedTableCards.put(TableLocation.NORTH, newArrayList(Card.CLUB_ACE, Card.CLUB_EIGHT));
        expectedTableCards.put(TableLocation.EAST, newArrayList(Card.CLUB_JACK, Card.DIAMOND_ACE, Card.DIAMOND_EIGHT));
        expectedTableCards.put(TableLocation.SOUTH, newArrayList(Card.CLUB_KING));
        expectedTableCards.put(TableLocation.WEST, newArrayList(Card.CLUB_QUEEN));

        game.putCards(Cardinal.NORTH, Card.CLUB_ACE, Card.CLUB_EIGHT);
        game.putCards(Cardinal.EAST, Card.CLUB_JACK, Card.DIAMOND_ACE, Card.DIAMOND_EIGHT);
        game.putCards(Cardinal.SOUTH, Card.CLUB_KING);
        game.putCards(Cardinal.WEST, Card.CLUB_QUEEN);
        assertReflectionEquals(expectedTableCards, game.getTableCards(), LENIENT_ORDER);
    }

    @Test
    public void testClearCards_AllTableLocations() throws Exception {
        game.putCards(Cardinal.NORTH, Card.CLUB_ACE, Card.CLUB_EIGHT);
        game.putCards(Cardinal.EAST, Card.CLUB_JACK, Card.DIAMOND_ACE, Card.DIAMOND_EIGHT);
        game.putCards(Cardinal.SOUTH, Card.CLUB_KING);
        game.putCards(Cardinal.WEST, Card.CLUB_QUEEN);
        game.clearCards();
        assertReflectionEquals(expectedTableCards, game.getTableCards());
    }

    @Test
    public void testClearCards_SomeTableLocations() throws Exception {
        expectedTableCards.put(TableLocation.NORTH, newArrayList(Card.CLUB_ACE, Card.CLUB_EIGHT));
        expectedTableCards.put(TableLocation.EAST, newArrayList(Card.CLUB_JACK, Card.DIAMOND_ACE, Card.DIAMOND_EIGHT));
        expectedTableCards.put(TableLocation.SOUTH, newArrayList(Card.CLUB_KING));

        game.putCards(Cardinal.NORTH, Card.CLUB_ACE, Card.CLUB_EIGHT);
        game.putCards(Cardinal.EAST, Card.CLUB_JACK, Card.DIAMOND_ACE, Card.DIAMOND_EIGHT);
        game.putCards(Cardinal.SOUTH, Card.CLUB_KING);
        game.putCards(Cardinal.WEST, Card.CLUB_QUEEN, Card.DIAMOND_QUEEN);

        game.clearCards(TableLocation.WEST);
        assertReflectionEquals(expectedTableCards, game.getTableCards(), LENIENT_ORDER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveCard_FromCenterWrongCard() throws Exception {
        game.moveCard(Card.CLUB_ACE, TableLocation.CENTER, TableLocation.EAST);
    }

    @Test
    public void testMoveCard_NorthCenterNorth() throws Exception {
        game.putCards(Cardinal.NORTH, Card.CLUB_ACE);
        game.moveCard(Card.CLUB_ACE, TableLocation.NORTH, TableLocation.CENTER);
        expectedTableCards.put(TableLocation.CENTER, newArrayList(Card.CLUB_ACE));
        assertReflectionEquals(expectedTableCards, game.getTableCards());
        game.moveCard(Card.CLUB_ACE, TableLocation.CENTER, TableLocation.NORTH);
        expectedTableCards.put(TableLocation.CENTER, Collections.<Card>emptyList());
        expectedTableCards.put(TableLocation.NORTH, newArrayList(Card.CLUB_ACE));
        assertReflectionEquals(expectedTableCards, game.getTableCards());
    }

    @Test
    public void testMoveCard_EastWest() throws Exception {
        game.putCards(Cardinal.EAST, Card.CLUB_ACE);
        game.moveCard(Card.CLUB_ACE, TableLocation.EAST, TableLocation.WEST);
        expectedTableCards.put(TableLocation.WEST, newArrayList(Card.CLUB_ACE));
        assertReflectionEquals(expectedTableCards, game.getTableCards());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveCard_FromNorthToNorth() throws Exception {
        game.putCards(Cardinal.NORTH, Card.CLUB_ACE);
        game.moveCard(Card.CLUB_ACE, TableLocation.NORTH, TableLocation.NORTH);
    }

    @Test
    public void testSetCardinalContract() throws Exception {
        assertReflectionEquals(expectedCardinalContracts, game.getCardinalContracts());
        game.setCardinalContract(Cardinal.EAST, Contract.MISER);
        expectedCardinalContracts.put(Cardinal.EAST, Contract.MISER);
        assertReflectionEquals(expectedCardinalContracts, game.getCardinalContracts());
    }

    @Test(expected = NullPointerException.class)
    public void testSetCardinalContract_NullCardinal() throws Exception {
        game.setCardinalContract(null, Contract.MISER);
    }

    @Test(expected = NullPointerException.class)
    public void testSetCardinalContract_NullContract() throws Exception {
        game.setCardinalContract(Cardinal.EAST, null);
    }

    @Test
    public void testGetCardinalTricks() throws Exception {
        assertReflectionEquals(expectedCardinalTricks, game.getCardinalTricks(), LENIENT_ORDER);
    }

    @Test
    public void testGetCardinalContracts() throws Exception {
        assertReflectionEquals(expectedCardinalContracts, game.getCardinalContracts(), LENIENT_ORDER);
    }

    @Test
    public void testGetPlayingContract() throws Exception {
        assertThat(game.getPlayingContract(), equalTo(Optional.<Contract>absent()));
    }

    @Test
    public void testGetTrump_Present() throws Exception {
        game.setCardinalContract(Cardinal.NORTH, Contract.EIGHT_CLUB);
        game.setCardinalContract(Cardinal.EAST, Contract.PASS);
        game.setCardinalContract(Cardinal.WEST, Contract.WHIST);
        assertThat(game.getTrump(), equalTo(Optional.of(Suit.CLUB)));
    }

    @Test
    public void testGetTrump_AllPass() throws Exception {
        game.setCardinalContract(Cardinal.NORTH, Contract.PASS);
        game.setCardinalContract(Cardinal.EAST, Contract.PASS);
        game.setCardinalContract(Cardinal.WEST, Contract.PASS);
        assertThat(game.getTrump(), equalTo(Optional.<Suit>absent()));
    }

    @Test
    public void testGetTrump_Absent() throws Exception {
        assertThat(game.getTrump(), equalTo(Optional.<Suit>absent()));
    }

    @Test
    public void testMoveCenterCardsToSluff_NoPlayingContract() throws Exception {
        game.setType(Game.Type.THREE_PLAYERS);
        game.putCards(Cardinal.NORTH, Card.CLUB_ACE);
        game.putCards(Cardinal.EAST, Card.CLUB_QUEEN);
        game.putCards(Cardinal.WEST, Card.CLUB_JACK);
        game.moveCard(Card.CLUB_ACE, TableLocation.NORTH, TableLocation.CENTER);
        game.moveCard(Card.CLUB_QUEEN, TableLocation.EAST, TableLocation.CENTER);
        game.moveCard(Card.CLUB_JACK, TableLocation.WEST, TableLocation.CENTER);

        game.moveCenterCardsToSluff();

        assertReflectionEquals(expectedCardinalTricks, game.getCardinalTricks());
        assertReflectionEquals(expectedTableCards, game.getTableCards());
    }

    @Test
    public void testMoveCenterCardsToSluff_HasPlayingContract() throws Exception {
        game.setType(Game.Type.THREE_PLAYERS);
        game.setCardinalContract(Cardinal.NORTH, Contract.EIGHT_DIAMOND);
        game.putCards(Cardinal.NORTH, Card.CLUB_ACE);
        game.putCards(Cardinal.EAST, Card.CLUB_QUEEN);
        game.putCards(Cardinal.WEST, Card.CLUB_JACK);
        game.moveCard(Card.CLUB_ACE, TableLocation.NORTH, TableLocation.CENTER);
        game.moveCard(Card.CLUB_QUEEN, TableLocation.EAST, TableLocation.CENTER);
        game.moveCard(Card.CLUB_JACK, TableLocation.WEST, TableLocation.CENTER);

        game.moveCenterCardsToSluff();

        expectedCardinalTricks.put(Cardinal.NORTH, 1);
        assertReflectionEquals(expectedCardinalTricks, game.getCardinalTricks());
        assertReflectionEquals(expectedTableCards, game.getTableCards());
    }
}
