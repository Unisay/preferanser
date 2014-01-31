package com.preferanser.server.resource;

import com.preferanser.server.service.DealService;
import com.preferanser.shared.domain.entity.Deal;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class DealResourceTest {

    private DealResource dealResource;

    @Mock
    private DealService dealService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dealResource = new DealResource(dealService);
    }

    @Test
    public void testGetCurrentUserOrSharedDeals() throws Exception {
        List<Deal> userDeals = newArrayList(new Deal(), new Deal());
        when(dealService.getCurrentUserOrSharedDeals()).thenReturn(userDeals);
        List<Deal> actualDeals = dealResource.getCurrentUserOrSharedDeals();
        verify(dealService).getCurrentUserOrSharedDeals();
        assertReflectionEquals(actualDeals, userDeals);
    }

    @Test
    public void testGetById() throws Exception {
        Deal deal = new Deal();
        deal.setId(1L);
        when(dealService.get(deal.getId())).thenReturn(deal);
        Deal actualDeal = dealResource.getById(deal.getId());
        verify(dealService).get(deal.getId());
        assertReflectionEquals(actualDeal, deal);
    }

    @Test
    public void testSave() throws Exception {
        Deal deal = new Deal();
        deal.setId(1L);
        when(dealService.save(deal)).thenReturn(deal);
        Long dealId = dealResource.save(deal);
        verify(dealService).save(deal);
        assertThat(dealId, equalTo(deal.getId()));
    }

    @Test
    public void testDelete() throws Exception {
        dealResource.delete(1L);
        verify(dealService).delete(1L);
    }

}
