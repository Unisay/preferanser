package com.preferanser.server.service;

import com.google.common.base.Optional;
import com.google.inject.Provider;
import com.preferanser.server.dao.DealDao;
import com.preferanser.server.dao.UserDao;
import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.server.exception.NotFoundException;
import com.preferanser.server.exception.UnauthorizedException;
import com.preferanser.shared.domain.*;
import com.preferanser.shared.util.Clock;
import com.preferanser.testng.ClockTestNGListener;
import com.preferanser.testng.DatastoreTestNGListener;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.Mockito.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@Listeners({ClockTestNGListener.class, DatastoreTestNGListener.class})
public class DealServiceTest {

    private DealService dealService;

    @Mock
    private DealDao dealDao;

    @Mock
    private UserDao userDao;

    @Mock
    private AuthenticationService authenticationService;

    private UserEntity user;
    private UserEntity otherUser;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dealService = new DealService(dealDao, userDao, new Provider<AuthenticationService>() {
            @Override public AuthenticationService get() {
                return authenticationService;
            }
        });
        user = new UserEntity();
        user.setId(123L);
        otherUser = new UserEntity();
        otherUser.setId(234L);
    }

    @Test
    public void testImportSharedDeals() throws Exception {
        List<DealEntity> sharedDeals = newArrayList(
            buildDealEntity(1, 333, "name1", true),
            buildDealEntity(2, 444, "name2", true),
            buildDealEntity(3, 555, "name3", true),
            buildDealEntity(4, user.getId(), "name4", true) // skip importing own shared deal
        );

        Set<DealEntity> importedDeals = newHashSet(
            buildDealEntity(null, user.getId(), "name1", false),
            buildDealEntity(null, user.getId(), "name2", false),
            buildDealEntity(null, user.getId(), "name3", false)
        );

        when(dealDao.getSharedDeals()).thenReturn(sharedDeals);

        dealService.importSharedDeals(user);

        verify(dealDao).getSharedDeals();
        verify(dealDao).save(importedDeals);
        verifyNoMoreInteractions(dealDao);
    }

    @Test
    public void testSave() throws Exception {
        DealEntity unsavedDeal = buildDealEntity(1L, null, "name1", false);
        DealEntity expectedUnsavedDeal = buildDealEntity(null, user.getId(), "name1", false);
        DealEntity expectedSavedDeal = buildDealEntity(1L, user.getId(), "name1", false);

        when(authenticationService.getCurrentUserOrThrow()).thenReturn(user);
        when(dealDao.save(expectedUnsavedDeal)).thenReturn(expectedSavedDeal);

        DealEntity savedDeal = dealService.save(unsavedDeal);

        verify(authenticationService).getCurrentUserOrThrow();
        verifyNoMoreInteractions(authenticationService);

        verify(dealDao).save(expectedUnsavedDeal);
        verifyNoMoreInteractions(dealDao);

        assertReflectionEquals(expectedSavedDeal, savedDeal);
    }

    @Test
    public void testSave_SharedByAdmin() throws Exception {
        user.setAdmin(true);
        DealEntity unsavedDeal = buildDealEntity(1L, null, "name1", true);
        DealEntity expectedUnsavedDeal = buildDealEntity(null, user.getId(), "name1", true);
        DealEntity expectedSavedDeal = buildDealEntity(1L, user.getId(), "name1", true);

        when(authenticationService.getCurrentUserOrThrow()).thenReturn(user);
        when(dealDao.save(expectedUnsavedDeal)).thenReturn(expectedSavedDeal);

        DealEntity savedDeal = dealService.save(unsavedDeal);

        verify(authenticationService).getCurrentUserOrThrow();
        verifyNoMoreInteractions(authenticationService);

        verify(dealDao).save(expectedUnsavedDeal);
        verifyNoMoreInteractions(dealDao);

        assertReflectionEquals(expectedSavedDeal, savedDeal);
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void testSave_NoAuthenticatedUser() throws Exception {
        when(authenticationService.getCurrentUserOrThrow()).thenThrow(new UnauthorizedException("Authentication required"));
        dealService.save(buildDealEntity(null, 111L, "name1", false));
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void testSave_NotAuthorizedUserUser() throws Exception {
        when(authenticationService.getCurrentUserOrThrow()).thenReturn(user);
        dealService.save(buildDealEntity(null, 111L, "name1", true));
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void testDelete_NotAuthenticated() throws Exception {
        when(authenticationService.getCurrentUserOrThrow()).thenThrow(new UnauthorizedException("Authentication required"));
        dealService.delete(1L);
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void testDelete_NotAuthorized() throws Exception {
        DealEntity deal = buildDealEntity(1, otherUser.getId(), "name1", false);

        when(authenticationService.getCurrentUserOrThrow()).thenReturn(user);
        when(dealDao.find(user, deal.getId())).thenReturn(Optional.of(deal));

        dealService.delete(deal.getId());
    }

    @Test
    public void testDelete() throws Exception {
        DealEntity deal = buildDealEntity(1L, user.getId(), "name1", false);

        when(authenticationService.getCurrentUserOrThrow()).thenReturn(user);
        when(dealDao.find(user, deal.getId())).thenReturn(Optional.of(deal));

        dealService.delete(deal.getId());

        verify(authenticationService).getCurrentUserOrThrow();
        verify(dealDao).deleteAsync(deal);
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void testUpdate_NoAuthenticatedUser() throws Exception {
        when(authenticationService.getCurrentUserOrThrow()).thenThrow(new UnauthorizedException("Authentication required"));
        dealService.update(buildDealEntity(1, user.getId(), "name1", false));
    }

    @Test(expectedExceptions = UnauthorizedException.class)
    public void testUpdate_NotAuthorizedUser() throws Exception {
        DealEntity deal = buildDealEntity(1, otherUser.getId(), "name1", false);

        when(authenticationService.getCurrentUserOrThrow()).thenReturn(user);
        when(dealDao.find(user, deal.getId())).thenReturn(Optional.of(deal));

        dealService.update(deal);

        verify(authenticationService).getCurrentUserOrThrow();
    }

    @Test
    public void testUpdate() throws Exception {
        DealEntity deal = buildDealEntity(1, user.getId(), "name1", false);

        when(authenticationService.getCurrentUserOrThrow()).thenReturn(user);
        when(dealDao.find(user, deal.getId())).thenReturn(Optional.of(deal));

        dealService.update(deal);

        verify(authenticationService).getCurrentUserOrThrow();
        DealEntity dealToUpdate = buildDealEntity(1, user.getId(), "name1", false);
        verify(dealDao).find(user, deal.getId());
        verify(dealDao).save(dealToUpdate);
        verifyNoMoreInteractions(dealDao);
    }

    @Test
    public void testGet_CurrentUser() throws Exception {
        DealEntity deal = buildDealEntity(1, 123, "name1", true);
        when(dealDao.find(user, deal.getId())).thenReturn(Optional.of(deal));

        when(authenticationService.getCurrentUserOrThrow()).thenReturn(user);
        DealEntity actualDeal = dealService.get(deal.getId());

        verify(dealDao).find(user, deal.getId());
        verifyNoMoreInteractions(dealDao);

        assertReflectionEquals(deal, actualDeal);
    }

    @Test
    public void testGet_User() throws Exception {
        // TODO: unit-test
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testGet_NotFound() throws Exception {
        long dealId = 9999999L;

        when(authenticationService.getCurrentUserOrThrow()).thenReturn(user);
        when(dealDao.find(user, dealId)).thenReturn(Optional.<DealEntity>absent());

        dealService.get(dealId);

        verify(authenticationService).getCurrentUserOrThrow();
        verify(dealDao).find(user, dealId);
    }

    @Test
    public void testGetCurrentUserOrSharedDeals_WithCurrentUser() throws Exception {
        List<DealEntity> userDeals = newArrayList(
            buildDealEntity(1, 111, "name1", true),
            buildDealEntity(2, 222, "name2", true),
            buildDealEntity(3, 333, "name3", true)
        );

        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(user));
        when(dealDao.getUserDeals(user)).thenReturn(userDeals);

        List<DealEntity> deals = dealService.getCurrentUserOrSharedDeals();

        verify(authenticationService).getCurrentUser();
        verifyNoMoreInteractions(authenticationService);

        verify(dealDao).getUserDeals(user);
        verifyNoMoreInteractions(dealDao);

        assertReflectionEquals(userDeals, deals);
    }

    @Test
    public void testGetCurrentUserOrSharedDeals_NoCurrentUser() throws Exception {
        List<DealEntity> sharedDeals = newArrayList(
            buildDealEntity(1, 111, "name1", true),
            buildDealEntity(2, 222, "name2", true),
            buildDealEntity(3, 222, "name3", true)
        );

        when(authenticationService.getCurrentUser()).thenReturn(Optional.<UserEntity>absent());
        when(dealDao.getSharedDeals()).thenReturn(sharedDeals);

        List<DealEntity> deals = dealService.getCurrentUserOrSharedDeals();

        verify(authenticationService).getCurrentUser();
        verifyNoMoreInteractions(authenticationService);

        verify(dealDao).getSharedDeals();
        verifyNoMoreInteractions(dealDao);

        assertReflectionEquals(sharedDeals, deals);
    }

    private DealEntity buildDealEntity(Number dealId, Number userId, String name, boolean shared) {

        DealEntity deal = new DealEntity();
        deal.setId(dealId == null ? null : dealId.longValue());
        deal.setShared(shared);

        if (userId != null) {
            UserEntity owner = new UserEntity();
            owner.setId(userId.longValue());
            deal.setOwner(owner);
        }

        deal.setName(name);
        deal.setDescription("description");
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
