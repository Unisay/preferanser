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
import com.preferanser.shared.domain.exception.validation.WrongNumCardsPerHandValidationError;
import com.preferanser.shared.util.Clock;
import org.apache.commons.lang.ArrayUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.preferanser.shared.domain.Card.CLUB_ACE;
import static com.preferanser.shared.domain.Hand.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class GameBuilderTest {

    private GameBuilder builder;
    private Card[] southCards;
    private Card[] eastCards;
    private Card[] westCards;
    private Deal deal;

    @BeforeMethod
    public void setUp() throws Exception {
        Clock.setNow(new Date(1));
        southCards = (Card[]) ArrayUtils.subarray(Card.values(), 0, 10);
        eastCards = (Card[]) ArrayUtils.subarray(Card.values(), 10, 20);
        westCards = (Card[]) ArrayUtils.subarray(Card.values(), 20, 30);
        builder = new GameBuilder()
                .setThreePlayers()
                .setWidow(Widow.fromArray((Card[]) ArrayUtils.subarray(Card.values(), 30, 32)))
                .setHandContract(SOUTH, Contract.SIX_SPADE)
                .setHandContract(EAST, Contract.WHIST)
                .setHandContract(WEST, Contract.PASS)
                .putCards(SOUTH, southCards)
                .putCards(EAST, eastCards)
                .putCards(WEST, westCards)
                .setFirstTurn(SOUTH);

        deal = new Deal();
        deal.setPlayers(Players.THREE);
        deal.setFirstTurn(SOUTH);
        deal.setCreated(Clock.getNow());
        deal.setName("name");
        deal.setWidow(Widow.fromArray((Card[]) ArrayUtils.subarray(Card.values(), 30, 32)));
        deal.setSouthCards(newHashSet(southCards));
        deal.setEastCards(newHashSet(eastCards));
        deal.setWestCards(newHashSet(westCards));
        deal.setSouthContract(Contract.SIX_SPADE);
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

    @Test
    public void testHandContract() throws Exception {
        Map<Hand, Contract> expectedContracts = ImmutableMap.of(
                SOUTH, Contract.SIX_SPADE,
                EAST, Contract.WHIST,
                WEST, Contract.PASS
        );

        Map<Hand, Contract> actualContracts = builder.build().getHandContracts();
        assertReflectionEquals(expectedContracts, actualContracts);
    }

    @Test
    public void testPutCards_Array() throws Exception {
        Game game = builder.build();

        assertThat(game.getCardsByHand(SOUTH), equalTo((Collection<Card>) newArrayList(southCards)));
        assertThat(game.getCardsByHand(EAST), equalTo((Collection<Card>) newArrayList(eastCards)));
        assertThat(game.getCardsByHand(WEST), equalTo((Collection<Card>) newArrayList(westCards)));
    }

    @Test(expectedExceptions = GameBuilderException.class, expectedExceptionsMessageRegExp = "" +
            ".*WrongNumCardsPerHandValidationError$")
    public void testClearCards() throws Exception {
        builder.clearCards(TableLocation.SOUTH);
        Map<Hand, Set<Card>> tableCards = builder.getHandCards();
        assertThat(tableCards.get(Hand.SOUTH), empty());
        assertThat(tableCards.get(Hand.EAST), not(empty()));
        assertThat(tableCards.get(Hand.WEST), not(empty()));
        builder.build();
    }

    @Test
    public void testClearCards_Center() throws Exception {
        builder.putCards(Hand.SOUTH, CLUB_ACE).moveCard(CLUB_ACE, TableLocation.SOUTH, TableLocation.CENTER);
        builder.clearCards(TableLocation.CENTER);
        Game game = builder.build();
        assertTrue(builder.getCenterCards().isEmpty(), "No cards in TableLocation.CENTER expected");
        assertTrue(game.getCenterCards().isEmpty(), "No cards in TableLocation.CENTER expected");
    }

    @Test(expectedExceptions = GameBuilderException.class, expectedExceptionsMessageRegExp = "" +
            ".*WrongNumCardsPerHandValidationError$")
    public void testClearCards_All() throws Exception {
        builder.clearCards();
        assertReflectionEquals(ImmutableMap.of(
                Hand.EAST, Collections.emptyList(),
                Hand.SOUTH, Collections.emptyList(),
                Hand.WEST, Collections.emptyList()
        ), builder.getHandCards());
        builder.build();
    }

    @Test(expectedExceptions = GameBuilderException.class, expectedExceptionsMessageRegExp
            = ".*NumPlayersNotSpecifiedValidationError.+FirstTurnNotSpecifiedValidationError" +
            ".+WrongNumberOfContractsValidationError$")
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
                .setFirstTurn(SOUTH)
                .build();
    }

    @Test(expectedExceptions = GameBuilderException.class,
            expectedExceptionsMessageRegExp = ".*WrongNumberOfContractsValidationError.*")
    public void testValidate_WrongNumberContracts() throws Exception {
        new GameBuilder()
                .setThreePlayers()
                .setFirstTurn(SOUTH)
                .setHandContract(EAST, Contract.PASS)
                .setHandContract(SOUTH, Contract.SEVEN_CLUB)
                .putCards(SOUTH, southCards)
                .putCards(EAST, eastCards)
                .putCards(WEST, westCards)
                .build();
    }

    @Test
    public void testValidate_HasConflictingContractsAllPasses() throws Exception {
        try {
            builder.setHandContract(SOUTH, Contract.PASS)
                    .setHandContract(EAST, Contract.PASS)
                    .setHandContract(WEST, Contract.PASS)
                    .build();
        } catch (GameBuilderException e) {
            List<? extends GameBuilderValidationError> expectedErrors = newArrayList(new
                    HasConflictingContractsValidationError());
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_HasConflictingContractsAllWhists() throws Exception {
        try {
            builder.setHandContract(SOUTH, Contract.WHIST)
                    .setHandContract(EAST, Contract.WHIST)
                    .setHandContract(WEST, Contract.WHIST)
                    .build();
        } catch (GameBuilderException e) {
            List<? extends GameBuilderValidationError> expectedErrors = newArrayList(new
                    HasConflictingContractsValidationError());
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_HasConflictingContractsPassPassWhist() throws Exception {
        try {
            builder.setHandContract(SOUTH, Contract.PASS)
                    .setHandContract(EAST, Contract.PASS)
                    .setHandContract(WEST, Contract.WHIST)
                    .build();
            fail("GameBuilderException expected");
        } catch (GameBuilderException e) {
            List<? extends GameBuilderValidationError> expectedErrors = newArrayList(new
                    HasConflictingContractsValidationError());
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_HasConflictingContractsPassWhistWhist() throws Exception {
        try {
            builder.setHandContract(SOUTH, Contract.PASS)
                    .setHandContract(EAST, Contract.WHIST)
                    .setHandContract(WEST, Contract.WHIST)
                    .build();
            fail("GameBuilderException expected");
        } catch (GameBuilderException e) {
            List<? extends GameBuilderValidationError> expectedErrors = newArrayList(new
                    HasConflictingContractsValidationError());
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test(expectedExceptions = GameBuilderException.class,
            expectedExceptionsMessageRegExp = ".*HasConflictingContractsValidationError$")
    public void testValidate_HasConflictingContracts() throws Exception {
        builder.setHandContract(SOUTH, Contract.SIX_DIAMOND)
                .setHandContract(EAST, Contract.SEVEN_CLUB)
                .setHandContract(WEST, Contract.WHIST)
                .build();
    }

    @Test
    public void testValidate_WrongHandCards() throws Exception {
        try {
            new GameBuilder()
                    .setFourPlayers()
                    .setFirstTurn(SOUTH)
                    .setHandContract(SOUTH, Contract.SIX_DIAMOND)
                    .setHandContract(EAST, Contract.PASS)
                    .setHandContract(WEST, Contract.WHIST)
                    .build();
            fail("WrongNumCardsPerHandValidationError expected");
        } catch (GameBuilderException e) {
            List<? extends GameBuilderValidationError> expectedErrors = newArrayList(
                    new WrongNumCardsPerHandValidationError(ImmutableMap.of(SOUTH, 0, EAST, 0, WEST, 0))
            );
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_WrongHandCards2() throws Exception {
        try {
            new GameBuilder()
                    .setFourPlayers()
                    .setFirstTurn(SOUTH)
                    .putCards(SOUTH, (Card[]) ArrayUtils.subarray(southCards, 0, 9))
                    .putCards(EAST, eastCards)
                    .putCards(WEST, westCards)
                    .setHandContract(SOUTH, Contract.SIX_DIAMOND)
                    .setHandContract(EAST, Contract.PASS)
                    .setHandContract(WEST, Contract.WHIST)
                    .build();
            fail("WrongNumCardsPerHandValidationError expected");
        } catch (GameBuilderException e) {
            List<? extends GameBuilderValidationError> expectedErrors = newArrayList(
                    new WrongNumCardsPerHandValidationError(ImmutableMap.of(SOUTH, 9))
            );
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_DuplicateCards() throws Exception {
        southCards[0] = eastCards[0];
        try {
            new GameBuilder()
                    .setFourPlayers()
                    .setFirstTurn(SOUTH)
                    .putCards(SOUTH, southCards)
                    .putCards(EAST, eastCards)
                    .putCards(WEST, westCards)
                    .setHandContract(SOUTH, Contract.SIX_DIAMOND)
                    .setHandContract(EAST, Contract.PASS)
                    .setHandContract(WEST, Contract.WHIST)
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
        assertThat(builder.getHandCards().get(Hand.SOUTH), hasItem(Card.SPADE_7));
        assertTrue(builder.moveCard(Card.SPADE_7, TableLocation.SOUTH, TableLocation.EAST));
        assertThat(builder.getHandCards().get(Hand.SOUTH), not(hasItem(Card.SPADE_7)));
        assertThat(builder.getHandCards().get(Hand.EAST), hasItem(Card.SPADE_7));
        assertTrue(builder.moveCard(Card.SPADE_7, TableLocation.SOUTH, TableLocation.CENTER));
        assertThat(builder.getHandCards().get(Hand.SOUTH), not(hasItem(Card.SPADE_7)));
        assertThat(builder.getCenterCards(), hasKey(Card.SPADE_7));
        assertThat(builder.getCenterCards().get(Card.SPADE_7), equalTo(Hand.SOUTH));
        assertTrue(builder.moveCard(Card.SPADE_7, TableLocation.CENTER, TableLocation.WEST));
        assertThat(builder.getCenterCards(), not(hasKey(Card.SPADE_7)));
        assertThat(builder.getHandCards().get(Hand.WEST), hasItem(Card.SPADE_7));
        assertTrue(builder.moveCard(Card.DIAMOND_9, TableLocation.EAST, TableLocation.CENTER));
        assertTrue(builder.moveCard(Card.SPADE_QUEEN, TableLocation.WEST, TableLocation.CENTER));
        assertTrue(builder.moveCard(Card.CLUB_7, TableLocation.SOUTH, TableLocation.CENTER));
        assertFalse(builder.moveCard(Card.DIAMOND_7, TableLocation.SOUTH, TableLocation.CENTER));
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