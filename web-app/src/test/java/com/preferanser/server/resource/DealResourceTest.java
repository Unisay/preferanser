package com.preferanser.server.resource;

import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.service.DealService;
import com.preferanser.server.transformer.DealInfoTransformer;
import com.preferanser.server.transformer.DealTransformer;
import com.preferanser.shared.domain.Deal;
import com.preferanser.shared.domain.DealInfo;
import com.preferanser.shared.util.Clock;
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

    @Mock
    private DealInfoTransformer dealInfoTransformer;

    private long userId1;
    private long dealId1;
    private long dealId2;
    private Deal deal1;
    private Deal deal2;
    private DealInfo dealInfo1;
    private DealInfo dealInfo2;
    private DealEntity dealEntity1;
    private DealEntity dealEntity2;
    private List<Deal> deals;
    private List<DealInfo> dealInfoList;
    private List<DealEntity> entities;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        dealResource = new DealResource(dealService, dealTransformer, dealInfoTransformer);
        userId1 = 111;
        dealId1 = 1;
        dealId2 = 2;
        deal1 = buildDeal(dealId1);
        deal2 = buildDeal(dealId2);
        dealInfo1 = buildDealInfo(dealId1);
        dealInfo2 = buildDealInfo(dealId2);
        dealEntity1 = buildDealEntity(dealId1);
        dealEntity2 = buildDealEntity(dealId2);

        deals = newArrayList(deal1, deal2);
        dealInfoList = newArrayList(dealInfo1, dealInfo2);
        entities = newArrayList(dealEntity1, dealEntity2);

        when(dealTransformer.fromEntity(dealEntity1)).thenReturn(deal1);
        when(dealTransformer.fromEntity(dealEntity2)).thenReturn(deal2);
        when(dealTransformer.toEntity(deal1)).thenReturn(dealEntity1);
        when(dealTransformer.toEntity(deal2)).thenReturn(dealEntity2);
        when(dealTransformer.fromEntities(entities)).thenReturn(deals);
        when(dealInfoTransformer.fromEntities(entities)).thenReturn(dealInfoList);
    }

    @Test
    public void testGetCurrentUserOrSharedDeals() throws Exception {
        when(dealService.getCurrentUserOrSharedDeals()).thenReturn(entities);

        List<DealInfo> actualDealInfoList = dealResource.getCurrentUserOrSharedDeals();

        verify(dealService).getCurrentUserOrSharedDeals();
        verify(dealInfoTransformer).fromEntities(entities);

        assertReflectionEquals(dealInfoList, actualDealInfoList);
    }

    @Test
    public void testGetById() throws Exception {
        when(dealService.get(userId1, dealId1)).thenReturn(dealEntity1);
        Deal actualDeal = dealResource.getById(userId1, dealId1);
        verify(dealService).get(userId1, dealId1);
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

    @Test
    public void testUpdate() throws Exception {
        // TODO: unit-test
    }

    private static Deal buildDeal(long id) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setName("name");
        deal.setDescription("description");
        return deal;
    }

    private DealInfo buildDealInfo(long id) {
        return new DealInfo(id, "name", "description", userId1, Clock.getNow());
    }

    private static DealEntity buildDealEntity(long id) {
        DealEntity entity = new DealEntity();
        entity.setId(id);
        entity.setName("name");
        entity.setDescription("description");
        return entity;
    }

}
