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

import com.google.common.collect.ImmutableMap;
import com.googlecode.objectify.Key;
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.entity.DealTestHelper;
import com.preferanser.shared.domain.entity.User;
import com.preferanser.shared.domain.exception.EditorException;
import com.preferanser.shared.domain.exception.validation.EditorValidationError;
import com.preferanser.shared.domain.exception.validation.HasConflictingContractsValidationError;
import com.preferanser.shared.domain.exception.validation.HasDuplicateCardsValidationError;
import com.preferanser.shared.domain.exception.validation.WrongNumCardsPerHandValidationError;
import com.preferanser.shared.util.Clock;
import com.preferanser.testng.ClockTestNGListener;
import com.preferanser.testng.DatastoreTestNGListener;
import org.apache.commons.lang.ArrayUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.preferanser.shared.domain.Card.*;
import static com.preferanser.shared.domain.Hand.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

@Listeners({ClockTestNGListener.class, DatastoreTestNGListener.class})
public class EditorTest {

    private Editor editor;
    private Card[] southCards;
    private Card[] eastCards;
    private Card[] westCards;
    private Widow widow;
    private String name;
    private String description;

    @BeforeMethod
    public void setUp() throws Exception {
        Clock.setNow(new Date(1));
        name = "name";
        description = "description";
        southCards = (Card[]) ArrayUtils.subarray(Card.values(), 0, 10);
        eastCards = (Card[]) ArrayUtils.subarray(Card.values(), 10, 20);
        westCards = (Card[]) ArrayUtils.subarray(Card.values(), 20, 30);
        editor = new Editor()
            .setName(name)
            .setDescription(description)
            .setThreePlayers()
            .setWidow(Widow.fromArray((Card[]) ArrayUtils.subarray(Card.values(), 30, 32)))
            .setHandContract(SOUTH, Contract.SIX_SPADE)
            .setHandContract(EAST, Contract.WHIST)
            .setHandContract(WEST, Contract.PASS)
            .putCards(SOUTH, southCards)
            .putCards(EAST, eastCards)
            .putCards(WEST, westCards)
            .setFirstTurn(SOUTH);

        widow = new Widow(Card.CLUB_KING, CLUB_QUEEN);
    }

    @Test
    public void testBuild_ThreePlayerGame() throws Exception {
        Deal deal = editor.setThreePlayers().build();
        assertThat(deal.getPlayers(), equalTo(Players.THREE));
    }

    @Test
    public void testBuild_FourPlayerGame() throws Exception {
        Deal deal = editor.setFourPlayers().build();
        assertThat(deal.getPlayers(), equalTo(Players.FOUR));
    }

    @Test
    public void testFirstTurn() throws Exception {
        assertThat(editor.setFirstTurn(EAST).build().getFirstTurn(), equalTo(EAST));
    }

    @Test
    public void testHandContract() throws Exception {
        Map<Hand, Contract> expectedContracts = ImmutableMap.of(
            SOUTH, Contract.SIX_SPADE,
            EAST, Contract.WHIST,
            WEST, Contract.PASS
        );

        Map<Hand, Contract> actualContracts = editor.build().getHandContracts();
        assertReflectionEquals(expectedContracts, actualContracts);
    }

    @Test
    public void testSetDeal() throws Exception {
        Deal originalDeal = DealTestHelper.buildDeal(1);

        Deal actualDeal = editor.setDeal(originalDeal).build();

        Deal expectedDeal = new Deal(originalDeal);
        expectedDeal.setOwner((Key<User>) null);
        assertLenientEquals(expectedDeal, actualDeal);
    }

    @Test
    public void testPutCards_Array() throws Exception {
        Deal deal = editor.build();
        assertReflectionEquals(newHashSet(southCards), deal.getSouthCards(), LENIENT_ORDER);
        assertReflectionEquals(newHashSet(eastCards), deal.getEastCards(), LENIENT_ORDER);
        assertReflectionEquals(newHashSet(westCards), deal.getWestCards(), LENIENT_ORDER);
    }

    @Test(expectedExceptions = EditorException.class, expectedExceptionsMessageRegExp = "" +
        ".*WrongNumCardsPerHandValidationError$")
    public void testClearCards() throws Exception {
        editor.clearCards(TableLocation.SOUTH);
        Map<Hand, Set<Card>> tableCards = editor.getHandCards();
        assertThat(tableCards.get(Hand.SOUTH), empty());
        assertThat(tableCards.get(Hand.EAST), not(empty()));
        assertThat(tableCards.get(Hand.WEST), not(empty()));
        editor.build();
    }

    @Test
    public void testClearCards_Center() throws Exception {
        editor.putCards(Hand.SOUTH, CLUB_ACE).moveCard(CLUB_ACE, TableLocation.CENTER);
        editor.clearCards(TableLocation.CENTER);
        assertTrue(editor.getCenterCards().isEmpty(), "No cards in TableLocation.CENTER expected");
    }

    @Test(expectedExceptions = EditorException.class, expectedExceptionsMessageRegExp = "" +
        ".*WrongNumCardsPerHandValidationError$")
    public void testClearCards_All() throws Exception {
        editor.clearCards();
        assertReflectionEquals(ImmutableMap.of(
            Hand.EAST, Collections.emptyList(),
            Hand.SOUTH, Collections.emptyList(),
            Hand.WEST, Collections.emptyList()
        ), editor.getHandCards());
        editor.build();
    }

    @Test(expectedExceptions = EditorException.class, expectedExceptionsMessageRegExp
        = ".*NumPlayersNotSpecifiedValidationError.+FirstTurnNotSpecifiedValidationError" +
        ".+WrongNumberOfContractsValidationError$")
    public void testValidate_NoPlayersNoTurnNoContracts() throws Exception {
        new Editor().build();
    }

    @Test(expectedExceptions = EditorException.class, expectedExceptionsMessageRegExp
        = ".*FirstTurnNotSpecifiedValidationError.+WrongNumberOfContractsValidationError$")
    public void testValidate_NoTurnNoContracts() throws Exception {
        new Editor()
            .setFourPlayers()
            .build();
    }

    @Test(expectedExceptions = EditorException.class,
        expectedExceptionsMessageRegExp = ".*WrongNumberOfContractsValidationError$")
    public void testValidate_NoContracts() throws Exception {
        new Editor()
            .setFourPlayers()
            .setFirstTurn(SOUTH)
            .build();
    }

    @Test(expectedExceptions = EditorException.class,
        expectedExceptionsMessageRegExp = ".*WrongNumberOfContractsValidationError.*")
    public void testValidate_WrongNumberContracts() throws Exception {
        new Editor()
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
            editor.setHandContract(SOUTH, Contract.PASS)
                .setHandContract(EAST, Contract.PASS)
                .setHandContract(WEST, Contract.PASS)
                .build();
        } catch (EditorException e) {
            List<? extends EditorValidationError> expectedErrors = newArrayList(new
                HasConflictingContractsValidationError());
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_HasConflictingContractsAllWhists() throws Exception {
        try {
            editor.setHandContract(SOUTH, Contract.WHIST)
                .setHandContract(EAST, Contract.WHIST)
                .setHandContract(WEST, Contract.WHIST)
                .build();
        } catch (EditorException e) {
            List<? extends EditorValidationError> expectedErrors = newArrayList(new
                HasConflictingContractsValidationError());
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_HasConflictingContractsPassPassWhist() throws Exception {
        try {
            editor.setHandContract(SOUTH, Contract.PASS)
                .setHandContract(EAST, Contract.PASS)
                .setHandContract(WEST, Contract.WHIST)
                .build();
            fail("EditorException expected");
        } catch (EditorException e) {
            List<? extends EditorValidationError> expectedErrors = newArrayList(new
                HasConflictingContractsValidationError());
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_HasConflictingContractsPassWhistWhist() throws Exception {
        try {
            editor.setHandContract(SOUTH, Contract.PASS)
                .setHandContract(EAST, Contract.WHIST)
                .setHandContract(WEST, Contract.WHIST)
                .build();
            fail("EditorException expected");
        } catch (EditorException e) {
            List<? extends EditorValidationError> expectedErrors = newArrayList(new
                HasConflictingContractsValidationError());
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test(expectedExceptions = EditorException.class,
        expectedExceptionsMessageRegExp = ".*HasConflictingContractsValidationError$")
    public void testValidate_HasConflictingContracts() throws Exception {
        editor.setHandContract(SOUTH, Contract.SIX_DIAMOND)
            .setHandContract(EAST, Contract.SEVEN_CLUB)
            .setHandContract(WEST, Contract.WHIST)
            .build();
    }

    @Test
    public void testValidate_WrongHandCards() throws Exception {
        try {
            new Editor()
                .setName(name)
                .setDescription(description)
                .setFourPlayers()
                .setWidow(widow)
                .setFirstTurn(SOUTH)
                .setHandContract(SOUTH, Contract.SIX_DIAMOND)
                .setHandContract(EAST, Contract.PASS)
                .setHandContract(WEST, Contract.WHIST)
                .build();
            fail("WrongNumCardsPerHandValidationError expected");
        } catch (EditorException e) {
            List<? extends EditorValidationError> expectedErrors = newArrayList(
                new WrongNumCardsPerHandValidationError(ImmutableMap.of(SOUTH, 0, EAST, 0, WEST, 0))
            );
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_WrongHandCards2() throws Exception {
        try {
            new Editor()
                .setName(name)
                .setDescription(description)
                .setFourPlayers()
                .setFirstTurn(SOUTH)
                .setWidow(widow)
                .putCards(SOUTH, (Card[]) ArrayUtils.subarray(southCards, 0, 9))
                .putCards(EAST, eastCards)
                .putCards(WEST, westCards)
                .setHandContract(SOUTH, Contract.SIX_DIAMOND)
                .setHandContract(EAST, Contract.PASS)
                .setHandContract(WEST, Contract.WHIST)
                .build();
            fail("WrongNumCardsPerHandValidationError expected");
        } catch (EditorException e) {
            List<? extends EditorValidationError> expectedErrors = newArrayList(
                new WrongNumCardsPerHandValidationError(ImmutableMap.of(SOUTH, 9))
            );
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testValidate_DuplicateCards() throws Exception {
        southCards[0] = eastCards[0];
        try {
            new Editor()
                .setName(name)
                .setDescription(description)
                .setFourPlayers()
                .setFirstTurn(SOUTH)
                .setWidow(widow)
                .putCards(SOUTH, southCards)
                .putCards(EAST, eastCards)
                .putCards(WEST, westCards)
                .setHandContract(SOUTH, Contract.SIX_DIAMOND)
                .setHandContract(EAST, Contract.PASS)
                .setHandContract(WEST, Contract.WHIST)
                .build();
            fail("HasDuplicateCardsValidationError expected");
        } catch (EditorException e) {
            List<? extends EditorValidationError> expectedErrors = newArrayList(
                new HasDuplicateCardsValidationError(Collections.singleton(eastCards[0]))
            );
            assertReflectionEquals(expectedErrors, e.getBuilderErrors());
        }
    }

    @Test
    public void testMoveCard() throws Exception {
        assertThat(editor.getHandCards().get(Hand.SOUTH), hasItem(SPADE_7));
        assertTrue(editor.moveCard(SPADE_7, TableLocation.EAST));
        assertThat(editor.getHandCards().get(Hand.SOUTH), not(hasItem(SPADE_7)));
        assertThat(editor.getHandCards().get(Hand.EAST), hasItem(SPADE_7));
        assertTrue(editor.moveCard(SPADE_7, TableLocation.CENTER));
        assertThat(editor.getHandCards().get(Hand.SOUTH), not(hasItem(SPADE_7)));
        assertThat(editor.getCenterCards(), hasKey(SPADE_7));
        assertThat(editor.getCenterCards().get(SPADE_7), equalTo(Hand.EAST));
        assertTrue(editor.moveCard(SPADE_7, TableLocation.WEST));
        assertThat(editor.getCenterCards(), not(hasKey(SPADE_7)));
        assertThat(editor.getHandCards().get(Hand.WEST), hasItem(SPADE_7));
        assertTrue(editor.moveCard(DIAMOND_9, TableLocation.CENTER));
        assertTrue(editor.moveCard(SPADE_QUEEN, TableLocation.CENTER));
        assertTrue(editor.moveCard(CLUB_7, TableLocation.CENTER));
        assertFalse(editor.moveCard(DIAMOND_7, TableLocation.CENTER));
    }

    @Test
    public void testReset() throws Exception {
        assertReflectionEquals(new Editor(), editor.reset());
    }

}