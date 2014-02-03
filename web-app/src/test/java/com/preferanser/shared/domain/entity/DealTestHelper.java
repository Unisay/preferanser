package com.preferanser.shared.domain.entity;

import com.preferanser.shared.domain.Contract;
import com.preferanser.shared.domain.Hand;
import com.preferanser.shared.domain.Players;
import com.preferanser.shared.domain.Widow;
import com.preferanser.shared.util.Clock;

import static com.google.common.collect.Sets.newHashSet;
import static com.preferanser.shared.domain.Card.*;

public abstract class DealTestHelper {

    private DealTestHelper() {
    }

    public static Deal buildDeal(long id, String userId, String description, String name) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setOwner(User.key(userId));
        deal.setName(name);
        deal.setDescription(description);
        deal.setCreated(Clock.getNow());
        deal.setPlayers(Players.THREE);
        deal.setCurrentTrickIndex(0);
        deal.setEastCards(newHashSet(
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
        ));
        deal.setWestCards(newHashSet(
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
        ));
        deal.setSouthCards(newHashSet(
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
        ));
        deal.setEastContract(Contract.PASS);
        deal.setWestContract(Contract.PASS);
        deal.setSouthContract(Contract.MISER);
        deal.setFirstTurn(Hand.EAST);
        deal.setWidow(new Widow(CLUB_ACE, CLUB_KING));
        return deal;
    }

    public static Deal buildDeal(long id) {
        return buildDeal(id, "userId", "description", "Miser of Sofia Kovalevskaia");
    }

}
