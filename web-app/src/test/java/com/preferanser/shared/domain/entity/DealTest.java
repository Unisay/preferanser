package com.preferanser.shared.domain.entity;

import com.preferanser.shared.domain.*;
import com.preferanser.shared.util.Clock;
import com.preferanser.testng.ClockTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@Listeners({ClockTestNGListener.class})
public class DealTest {

    @Test
    public void testEquals_Positive() throws Exception {
        Deal deal1 = buildDeal(1L, "userId", "description", "name");
        Deal deal2 = buildDeal(1L, "userId", "description", "name");
        assertThat(deal1, equalTo(deal2));
    }

    @Test
    public void testEquals_PositiveCardsOrder() throws Exception {
        Deal deal1 = buildDeal(1L, "userId", "description", "name");
        Deal deal2 = buildDeal(1L, "userId", "description", "name");
        deal2.setEastCards(newHashSet(Card.CLUB_9, Card.CLUB_8, Card.CLUB_7));
        assertThat(deal1, equalTo(deal2));
    }

    @Test
    public void testEquals_Negative() throws Exception {
        Deal deal1 = buildDeal(1L, "userId", "description", "name");
        Deal deal2 = buildDeal(2L, "userId", "description", "name");
        Deal deal3 = buildDeal(1L, "userId3", "description", "name");
        Deal deal4 = buildDeal(1L, "userId", "description4", "name");
        assertThat(deal1, not(equalTo(deal2)));
        assertThat(deal1, not(equalTo(deal3)));
        assertThat(deal1, not(equalTo(deal4)));
    }

    @Test
    public void testCopyConstructor() throws Exception {
        Deal deal1 = buildDeal(1L, "userId", "description", "name");
        Deal deal2 = new Deal(deal1);
        assertThat(deal1, equalTo(deal2));
    }

    private static Deal buildDeal(long id, String userId, String description, String name) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setUserId(userId);
        deal.setName(name);
        deal.setDescription(description);
        deal.setCreated(Clock.getNow());
        deal.setPlayers(Players.THREE);
        deal.setCurrentTrickIndex(0);
        deal.setEastCards(newHashSet(Card.CLUB_7, Card.CLUB_8, Card.CLUB_9));
        deal.setWestCards(newHashSet(Card.SPADE_7, Card.SPADE_8, Card.SPADE_9));
        deal.setSouthCards(newHashSet(Card.HEART_7, Card.HEART_8, Card.HEART_9));
        deal.setEastContract(Contract.EIGHT_CLUB);
        deal.setWestContract(Contract.PASS);
        deal.setSouthContract(Contract.PASS);
        deal.setFirstTurn(Hand.EAST);
        deal.setWidow(new Widow(Card.CLUB_ACE, Card.DIAMOND_ACE));
        return deal;
    }

}