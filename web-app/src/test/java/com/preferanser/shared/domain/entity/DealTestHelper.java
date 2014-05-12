package com.preferanser.shared.domain.entity;

import com.preferanser.server.entity.DealEntity;
import com.preferanser.shared.domain.*;
import com.preferanser.shared.util.Clock;

import java.util.HashSet;

import static com.google.common.collect.Sets.newHashSet;
import static com.preferanser.shared.domain.Card.*;

public abstract class DealTestHelper {

    public static final String DESCRIPTION = "description";
    public static final String NAME = "Miser of Sofia Kovalevskaia";
    public static final int CURRENT_TRICK_INDEX = 3;
    public static final Hand FIRST_TURN = Hand.EAST;
    public static final Widow WIDOW = new Widow(CLUB_ACE, CLUB_KING);
    public static final Contract EAST_CONTRACT = Contract.PASS;
    public static final Contract WEST_CONTRACT = Contract.PASS;
    public static final Contract SOUTH_CONTRACT = Contract.MISER;
    public static final HashSet<Card> SOUTH_CARDS = newHashSet(
        SPADE_9,
        SPADE_7,
        HEART_9,
        SPADE_8,
        CLUB_8,
        DIAMOND_8,
        HEART_8,
        DIAMOND_9,
        DIAMOND_7,
        SPADE_10
    );
    public static final HashSet<Card> WEST_CARDS = newHashSet(
        SPADE_KING,
        CLUB_QUEEN,
        CLUB_JACK,
        DIAMOND_ACE,
        HEART_10,
        CLUB_10,
        SPADE_ACE,
        HEART_KING,
        HEART_ACE,
        HEART_QUEEN
    );
    public static final HashSet<Card> EAST_CARDS = newHashSet(
        DIAMOND_JACK,
        DIAMOND_10,
        SPADE_QUEEN,
        CLUB_9,
        CLUB_7,
        DIAMOND_QUEEN,
        DIAMOND_KING,
        SPADE_JACK,
        HEART_JACK,
        HEART_7
    );

    private DealTestHelper() {
    }

    public static Deal buildDeal(long id, String name, String description) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setName(name);
        deal.setDescription(description);
        deal.setCreated(Clock.getNow());
        deal.setPlayers(Players.THREE);
        deal.setCurrentTrickIndex(CURRENT_TRICK_INDEX);
        deal.setEastCards(EAST_CARDS);
        deal.setWestCards(WEST_CARDS);
        deal.setSouthCards(SOUTH_CARDS);
        deal.setEastContract(EAST_CONTRACT);
        deal.setWestContract(WEST_CONTRACT);
        deal.setSouthContract(SOUTH_CONTRACT);
        deal.setFirstTurn(FIRST_TURN);
        deal.setWidow(WIDOW);
        return deal;
    }

    public static Deal buildDeal(long id) {
        return buildDeal(id, NAME, DESCRIPTION);
    }

    public static Deal buildDeal() {
        return buildDeal(1L);
    }

    public static DealEntity buildDealEntity() {
        DealEntity entity = new DealEntity();
        entity.setId(1L);
        entity.setName(NAME);
        entity.setDescription(DESCRIPTION);
        entity.setCreated(Clock.getNow());
        entity.setPlayers(Players.THREE);
        entity.setCurrentTrickIndex(CURRENT_TRICK_INDEX);
        entity.setFirstTurn(FIRST_TURN);
        entity.setWidow(WIDOW);
        entity.setEastContract(EAST_CONTRACT);
        entity.setWestContract(WEST_CONTRACT);
        entity.setSouthContract(SOUTH_CONTRACT);
        entity.setSouthCards(SOUTH_CARDS);
        entity.setEastCards(EAST_CARDS);
        entity.setWestCards(WEST_CARDS);
        return entity;
    }

}
