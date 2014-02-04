package com.preferanser.server.resource;

import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.service.DealService;
import com.preferanser.server.transformer.DealTransformer;
import com.preferanser.shared.domain.Deal;
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

    @Mock
    private DealTransformer dealTransformer;

    private long dealId1;
    private long dealId2;
    private Deal deal1;
    private Deal deal2;
    private DealEntity dealEntity1;
    private DealEntity dealEntity2;
    private List<Deal> deals;
    private List<DealEntity> entities;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        dealResource = new DealResource(dealService, dealTransformer);
        dealId1 = 1;
        dealId2 = 2;
        deal1 = buildDeal(dealId1);
        deal2 = buildDeal(dealId2);
        dealEntity1 = buildDealEntity(dealId1);
        dealEntity2 = buildDealEntity(dealId2);

        deals = newArrayList(deal1, deal2);
        entities = newArrayList(dealEntity1, dealEntity2);

        when(dealTransformer.fromEntity(dealEntity1)).thenReturn(deal1);
        when(dealTransformer.fromEntity(dealEntity2)).thenReturn(deal2);
        when(dealTransformer.toEntity(deal1)).thenReturn(dealEntity1);
        when(dealTransformer.toEntity(deal2)).thenReturn(dealEntity2);
        when(dealTransformer.fromEntities(entities)).thenReturn(deals);
    }

    @Test
    public void testGetCurrentUserOrSharedDeals() throws Exception {
        when(dealService.getCurrentUserOrSharedDeals()).thenReturn(entities);

        List<Deal> actualDeals = dealResource.getCurrentUserOrSharedDeals();

        verify(dealService).getCurrentUserOrSharedDeals();
        verify(dealTransformer).fromEntities(entities);

        assertReflectionEquals(deals, actualDeals);
    }

    @Test
    public void testGetById() throws Exception {
        when(dealService.get(dealId1)).thenReturn(dealEntity1);
        Deal actualDeal = dealResource.getById(dealId1);
        verify(dealService).get(dealId1);
        assertReflectionEquals(actualDeal, deal1);
    }

    @Test
    public void testSave() throws Exception {
        when(dealService.save(dealEntity1)).thenReturn(dealEntity2);
        Long savedDealId = dealResource.save(deal1);
        verify(dealService).save(dealEntity1);
        assertThat(savedDealId, equalTo(dealId2));
    }

    @Test
    public void testDelete() throws Exception {
        dealResource.delete(1L);
        verify(dealService).delete(1L);
    }

    private static Deal buildDeal(long id) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setName("name");
        deal.setDescription("description");
        return deal;
    }

    private static DealEntity buildDealEntity(long id) {
        DealEntity entity = new DealEntity();
        entity.setId(id);
        entity.setName("name");
        entity.setDescription("description");
        return entity;
    }

}
