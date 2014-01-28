package com.preferanser.shared.domain.entity;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class DealTest {

    private Deal deal;

    @BeforeMethod
    public void setUp() throws Exception {
        deal = buildDeal(1L, "userId", "description", "name");
    }

    @Test
    public void testEquals_Positive() throws Exception {
        Deal otherDeal = buildDeal(1L, "userId", "description", "name");

        assertThat(deal, equalTo(deal));
        assertThat(deal, equalTo(otherDeal));
        // assertReflectionEquals(deal, new Deal(deal));
        assertThat(deal, equalTo(new Deal(deal)));
    }

    @Test
    public void testEquals_Negative() throws Exception {
        Deal otherDeal = buildDeal(1L, "userId2", "description", "name");
        assertThat(deal, not(equalTo(otherDeal)));
    }

    private static Deal buildDeal(long id, String userId, String description, String name) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setUserId(userId);
        deal.setName(name);
        deal.setDescription(description);
        return deal;
    }

}