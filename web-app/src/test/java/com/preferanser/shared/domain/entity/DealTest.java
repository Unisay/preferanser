package com.preferanser.shared.domain.entity;

import com.preferanser.shared.domain.Deal;
import com.preferanser.testng.ClockTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.preferanser.shared.domain.entity.DealTestHelper.buildDeal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@Listeners({ClockTestNGListener.class})
public class DealTest {

    @Test
    public void testEquals_Positive() throws Exception {
        Deal deal1 = buildDeal(1L, "name", "description");
        Deal deal2 = buildDeal(1L, "name", "description");
        assertThat(deal1, equalTo(deal2));
    }

    @Test
    public void testEquals_Negative() throws Exception {
        Deal deal1 = buildDeal(1L, "name", "description");
        Deal deal2 = buildDeal(2L, "name", "description");
        Deal deal3 = buildDeal(1L, "name_other", "description");
        Deal deal4 = buildDeal(1L, "name", "description_other");
        assertThat(deal1, not(equalTo(deal2)));
        assertThat(deal1, not(equalTo(deal3)));
        assertThat(deal1, not(equalTo(deal4)));
    }

    @Test
    public void testCopyConstructor() throws Exception {
        Deal deal1 = buildDeal(1L, "name", "description");
        Deal deal2 = new Deal(deal1);
        assertThat(deal1, equalTo(deal2));
    }

}