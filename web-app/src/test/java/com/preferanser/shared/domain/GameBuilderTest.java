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

package com.preferanser.shared.domain;

import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableMap;
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.exception.GameBuilderException;
import com.preferanser.shared.domain.exception.validation.GameBuilderValidationError;
import com.preferanser.shared.domain.exception.validation.HasConflictingContractsValidationError;
import com.preferanser.shared.domain.exception.validation.HasDuplicateCardsValidationError;
import com.preferanser.shared.domain.exception.validation.WrongNumCardsPerCardinalValidationError;
import com.preferanser.shared.util.Clock;
import org.apache.commons.lang.ArrayUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.preferanser.shared.domain.Card.CLUB_ACE;
import static com.preferanser.shared.domain.Cardinal.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class GameBuilderTest {

    private GameBuilder builder;
    private Card[] northCards;
    private Card[] eastCards;
    private Card[] westCards;
    private Deal deal;

    @BeforeMethod
    public void setUp() throws Exception {
        Clock.setNow(new Date(1));
        northCards = (Card[]) ArrayUtils.subarray(Card.values(), 0, 10);
        eastCards = (Card[]) ArrayUtils.subarray(Card.values(), 10, 20);
        westCards = (Card[]) ArrayUtils.subarray(Card.values(), 20, 30);
        builder = new GameBuilder()
            .setThreePlayers()
            .setWidow(Widow.fromArray((Card[]) ArrayUtils.subarray(Card.values(), 30, 32)))
            .setCardinalContract(NORTH, Contract.SIX_SPADE)
            .setCardinalContract(EAST, Contract.WHIST)
            .setCardinalContract(WEST, Contract.PASS)
            .putCards(NORTH, northCards)
            .putCards(EAST, eastCards)
            .putCards(WEST, westCards)
            .setFirstTurn(NORTH);

        deal = new Deal();
        deal.setPlayers(Players.THREE);
        deal.setFirstTurn(NORTH);
        deal.setCreated(Clock.getNow());
        deal.setName("name");
        deal.setWidow(Widow.fromArray((Card[]) ArrayUtils.subarray(Card.values(), 30, 32)));
        deal.setNorthCards(newArrayList(northCards));
        deal.setEastCards(newArrayList(eastCards));
        deal.setWestCards(newArrayList(westCards));
        deal.setNorthContract(Contract.SIX_SPADE);
        deal.setEastContract(Contract.WHIST);
        deal.setWestContract(Contract.PASS);
    }

    @Test
    public void testBuild_ThreePlayerGame() throws Exception {
        Game game = builder.setThreePlayers().build();
        assertThat(game.getPlayers(), equalTo(Players.THREE));
    }

    @Test
    public void testBuild_FourPlayerGame() throws Exception {
        Game game = builder.setFourPlayers().build();
        assertThat(game.getPlayers(), equalTo(Players.FOUR));
    }

    @Test
    public void testFirstTurn() throws Exception {
        assertThat(builder.setFirstTurn(EAST).build().getTurn(), equalTo(EAST));
    }

    @Test(expectedExceptions = GameBuilderException.class, expectedExceptionsMessageRegExp = ".*WrongFirstTurnValidationError$")
    public void testFirstTurn_Wrong() throws Exception {
        builder.setFirstTurn(SOUTH).build();
    }

    @Test
    public void testCardinalContract() throws Exception {
        Map<Cardinal, Contract> expectedContracts = newHashMap();
        expectedContracts.put(NORTH, Contract.SIX_SPADE);
        expectedContracts.put(EAST, Contract.WHIST);
        expectedContracts.put(WEST, Contract.PASS);

        Map<Cardinal, Contract> actualContracts = builder.build().getCardinalContracts();
        assertReflectionEquals(expectedContracts, actualContracts);
        assertThat(actualContracts, not(hasKey(SOUTH)));
    }

    @Test
    public void testPutCards_Array() throws Exception {
        Game game = builder.build();

        assertThat(game.getCardsByCardinal(NORTH), equalTo((Collection<Card>) newArrayList(northCards)));
        assertThat(game.getCardsByCardinal(EAST), equalTo((Collection<Card>) newArrayList(eastCards)));
        assertThat(game.getCardsByCardinal(WEST), equalTo((Collection<Card>) newArrayList(westCards)));
        assertThat(game.getCardsByCardinal(SOUTH), empty());
    }

    @Test(expectedExceptions = GameBuilderException.class, expectedExceptionsMessageRegExp = ".*WrongNumCardsPerCardinalValidationError$")
    public void testClearCards() throws Exception {
        builder.clearCards(TableLocation.NORTH);
        Map<TableLocation, Collection<Card>> tableCards = builder.getTableCards();
        assertThat(tableCards, not(hasKey(TableLocation.NORTH)));
        assertThat(tableCards.get(TableLocation.EAST), not(empty()));
        assertThat(tableCards.get(TableLocation.WEST), not(empty()));
        assertThat(tableCards.get(TableLocation.SOUTH), not(empty()));
        builder.build();
    }

    @Test
    public void testClearCards_Center() throws Exception {
        builder.putCards(Cardinal.NORTH, CLUB_ACE).moveCard(CLUB_ACE, TableLocation.NORTH, TableLocation.CENTER);
        builder.clearCards(TableLocation.CENTER);
        Game game = builder.build();
        assertTrue(builder.getCenterCards().isEmpty(), "No cards in TableLocation.CENTER expected");
        assertTrue(game.getCenterCards().isEmpty(), "No cards in TableLocation.CENTER expected");
    }

    @Test(expectedExceptions = GameBuilderException.class, expectedExceptionsMessageRegExp = ".*WrongNumCardsPerCardinalValidationError$")
    public void testClearCards_All() throws Exception {
        builder.clearCards();
        assertTrue(builder.getTableCards().isEmpty());
        builder.build();
    }

    @Test(expectedExceptions = GameBuilderException.class, expectedExceptionsMessageRegExp
        = ".*NumPlayersNotSpecifiedValidationError.+FirstTurnNotSpecifiedValidationError.+WrongNumberOfContractsValidationError$")
    public void testValidate_NoPlayersNoTurnNoContracts() throws Exception {
        new GameBuilder().build();
    }

    @Test(expectedExceptions = GameBuilderException.class, expectedExceptionsMessageRegExp
        = ".*FirstTurnNotSpecifiedValidationError.+WrongNumberOfContractsValidationError$")
    public void testValidate_NoTurnNoContracts() throws Exception {
        new GameBuilder()
            .setFourPlayers()
            .build();
    }

    @Test(expectedExceptions = GameBuilderException.class,
        expectedExceptionsMessageRegExp = ".*WrongNumberOfContractsValidationError$")
    public void testValidate_NoContracts() throws Exception {
        new GameBuilder()
            .setFourPlayers()
            .setFirstTurn(NORTH)
            .build();
    }

    @Test(expectedExceptions = GameBuilderException.class,
        expectedExceptionsMessageRegExp = ".*WrongNumberOfContractsValidationError.*")
    public void testValidate_WrongNumberContracts() throws Exception {
        new GameBuilder()
            .setThreePlayers()
            .setFirstTurn(NORTH)
            .setCardinalContract(NORTH, Contract.SEVEN_CLUB)
            .setCardinalContract(EAST, Contract.PASS)
            .setCardinalContract(SOUTH, Contract.PASS)
            .setCardinalContract(WEST, Contract.PASS)
            .putCards(NORTH, northCards)
            .putCards(EAST, eastCards)
            .putCards(WEST, westCards)
            .build();
    }

    @Test
    public void testValidate_HasConflictingContractsAllPasses() throws Exception {
        try {
            builder.setCardinalContract(NORTH, Contract.PASS)
                .setCardinalContract(EAST, Contract.PASS)
                .setCardinalContract(WEST, Contract.PASS)
                .build();
        } catch (GameBuilderException e) {
            List<? extends GameBuilderValidationError> expectedErrors = newArrayList(new HasConflictingContractsValidationError());
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_HasConflictingContractsAllWhists() throws Exception {
        try {
            builder.setCardinalContract(NORTH, Contract.WHIST)
                .setCardinalContract(EAST, Contract.WHIST)
                .setCardinalContract(WEST, Contract.WHIST)
                .build();
        } catch (GameBuilderException e) {
            List<? extends GameBuilderValidationError> expectedErrors = newArrayList(new HasConflictingContractsValidationError());
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_HasConflictingContractsPassPassWhist() throws Exception {
        try {
            builder.setCardinalContract(NORTH, Contract.PASS)
                .setCardinalContract(EAST, Contract.PASS)
                .setCardinalContract(WEST, Contract.WHIST)
                .build();
            fail("GameBuilderException expected");
        } catch (GameBuilderException e) {
            List<? extends GameBuilderValidationError> expectedErrors = newArrayList(new HasConflictingContractsValidationError());
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_HasConflictingContractsPassWhistWhist() throws Exception {
        try {
            builder.setCardinalContract(NORTH, Contract.PASS)
                .setCardinalContract(EAST, Contract.WHIST)
                .setCardinalContract(WEST, Contract.WHIST)
                .build();
            fail("GameBuilderException expected");
        } catch (GameBuilderException e) {
            List<? extends GameBuilderValidationError> expectedErrors = newArrayList(new HasConflictingContractsValidationError());
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test(expectedExceptions = GameBuilderException.class,
        expectedExceptionsMessageRegExp = ".*HasConflictingContractsValidationError$")
    public void testValidate_HasConflictingContracts() throws Exception {
        builder.setCardinalContract(NORTH, Contract.SIX_DIAMOND)
            .setCardinalContract(EAST, Contract.SEVEN_CLUB)
            .setCardinalContract(WEST, Contract.WHIST)
            .build();
    }

    @Test
    public void testValidate_WrongCardinalCards() throws Exception {
        try {
            new GameBuilder()
                .setFourPlayers()
                .setFirstTurn(NORTH)
                .setCardinalContract(NORTH, Contract.SIX_DIAMOND)
                .setCardinalContract(EAST, Contract.PASS)
                .setCardinalContract(WEST, Contract.WHIST)
                .build();
            fail("WrongNumCardsPerCardinalValidationError expected");
        } catch (GameBuilderException e) {
            List<? extends GameBuilderValidationError> expectedErrors = newArrayList(
                new WrongNumCardsPerCardinalValidationError(ImmutableMap.of(NORTH, 0, EAST, 0, WEST, 0))
            );
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_WrongCardinalCards2() throws Exception {
        try {
            new GameBuilder()
                .setFourPlayers()
                .setFirstTurn(NORTH)
                .putCards(NORTH, (Card[]) ArrayUtils.subarray(northCards, 0, 9))
                .putCards(EAST, eastCards)
                .putCards(WEST, westCards)
                .setCardinalContract(NORTH, Contract.SIX_DIAMOND)
                .setCardinalContract(EAST, Contract.PASS)
                .setCardinalContract(WEST, Contract.WHIST)
                .build();
            fail("WrongNumCardsPerCardinalValidationError expected");
        } catch (GameBuilderException e) {
            List<? extends GameBuilderValidationError> expectedErrors = newArrayList(
                new WrongNumCardsPerCardinalValidationError(ImmutableMap.of(NORTH, 9))
            );
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_DuplicateCards() throws Exception {
        northCards[0] = eastCards[0];
        try {
            new GameBuilder()
                .setFourPlayers()
                .setFirstTurn(NORTH)
                .putCards(NORTH, northCards)
                .putCards(EAST, eastCards)
                .putCards(WEST, westCards)
                .setCardinalContract(NORTH, Contract.SIX_DIAMOND)
                .setCardinalContract(EAST, Contract.PASS)
                .setCardinalContract(WEST, Contract.WHIST)
                .build();
            fail("HasDuplicateCardsValidationError expected");
        } catch (GameBuilderException e) {
            List<? extends GameBuilderValidationError> expectedErrors = newArrayList(
                new HasDuplicateCardsValidationError(Collections.singleton(eastCards[0]))
            );
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testMoveCard() throws Exception {
        assertThat(builder.getTableCards().get(TableLocation.NORTH), hasItem(Card.SPADE_7));
        assertTrue(builder.moveCard(Card.SPADE_7, TableLocation.NORTH, TableLocation.SOUTH));
        assertThat(builder.getTableCards().get(TableLocation.NORTH), not(hasItem(Card.SPADE_7)));
        assertThat(builder.getTableCards().get(TableLocation.SOUTH), hasItem(Card.SPADE_7));
        assertTrue(builder.moveCard(Card.SPADE_7, TableLocation.SOUTH, TableLocation.CENTER));
        assertThat(builder.getTableCards().get(TableLocation.SOUTH), not(hasItem(Card.SPADE_7)));
        assertThat(builder.getCenterCards(), hasKey(Card.SPADE_7));
        assertThat(builder.getCenterCards().get(Card.SPADE_7), equalTo(Cardinal.SOUTH));
        assertTrue(builder.moveCard(Card.SPADE_7, TableLocation.CENTER, TableLocation.WEST));
        assertThat(builder.getCenterCards(), not(hasKey(Card.SPADE_7)));
        assertThat(builder.getTableCards().get(TableLocation.WEST), hasItem(Card.SPADE_7));
        assertTrue(builder.moveCard(Card.DIAMOND_9, TableLocation.EAST, TableLocation.CENTER));
        assertTrue(builder.moveCard(Card.SPADE_QUEEN, TableLocation.WEST, TableLocation.CENTER));
        assertTrue(builder.moveCard(Card.CLUB_7, TableLocation.NORTH, TableLocation.CENTER));
        assertFalse(builder.moveCard(Card.DIAMOND_7, TableLocation.NORTH, TableLocation.CENTER));
    }

    @Test
    public void testBuildDeal() throws Exception {
        Deal actualDeal = builder.buildDeal("name");
        assertReflectionEquals(deal, actualDeal);
    }

    @Test
    public void testBuildFromDeal() throws Exception {
        GameBuilder actualBuilder = new GameBuilder().setDeal(deal);
        assertReflectionEquals(builder, actualBuilder);
    }

}