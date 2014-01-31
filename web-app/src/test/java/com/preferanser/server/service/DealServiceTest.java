package com.preferanser.server.service;

import com.google.common.base.Optional;
import com.google.inject.Provider;
import com.preferanser.server.dao.DealDao;
import com.preferanser.server.exception.NoAuthenticatedUserException;
import com.preferanser.server.exception.NotAuthorizedUserException;
import com.preferanser.shared.domain.*;
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.entity.User;
import com.preferanser.shared.util.Clock;
import com.preferanser.testng.ClockTestNGListener;
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

@Listeners({ClockTestNGListener.class})
public class DealServiceTest {

    private DealService dealService;

    private User user;

    @Mock
    private DealDao dealDao;

    @Mock
    private AuthenticationService authenticationService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dealService = new DealService(dealDao, new Provider<AuthenticationService>() {
            @Override public AuthenticationService get() {
                return authenticationService;
            }
        });
        user = new User();
        user.setGoogleId("googleId");
    }

    @Test
    public void testImportSharedDeals() throws Exception {
        List<Deal> sharedDeals = newArrayList(
            buildDeal(1L, "admin1", "name1", true),
            buildDeal(2L, "admin2", "name2", true),
            buildDeal(3L, "admin2", "name3", true),
            buildDeal(4L, user.getGoogleId(), "name4", true) // skip importing own shared deal
        );

        Set<Deal> importedDeals = newHashSet(
            buildDeal(null, user.getGoogleId(), "name1", false),
            buildDeal(null, user.getGoogleId(), "name2", false),
            buildDeal(null, user.getGoogleId(), "name3", false)
        );

        when(dealDao.getSharedDeals()).thenReturn(sharedDeals);

        dealService.importSharedDeals(user);

        verify(dealDao).getSharedDeals();
        verify(dealDao).save(importedDeals);
        verifyNoMoreInteractions(dealDao);
    }

    @Test
    public void testSave() throws Exception {
        Deal unsavedDeal = buildDeal(1L, null, "name1", false);
        Deal expectedUnsavedDeal = buildDeal(null, user.getGoogleId(), "name1", false);
        Deal expectedSavedDeal = buildDeal(1L, user.getGoogleId(), "name1", false);

        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(user));
        when(dealDao.save(expectedUnsavedDeal)).thenReturn(expectedSavedDeal);

        Deal savedDeal = dealService.save(unsavedDeal);

        verify(authenticationService).getCurrentUser();
        verifyNoMoreInteractions(authenticationService);

        verify(dealDao).save(expectedUnsavedDeal);
        verifyNoMoreInteractions(dealDao);

        assertReflectionEquals(expectedSavedDeal, savedDeal);
    }

    @Test
    public void testSave_SharedByAdmin() throws Exception {
        user.setAdmin(true);
        Deal unsavedDeal = buildDeal(1L, null, "name1", true);
        Deal expectedUnsavedDeal = buildDeal(null, user.getGoogleId(), "name1", true);
        Deal expectedSavedDeal = buildDeal(1L, user.getGoogleId(), "name1", true);

        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(user));
        when(dealDao.save(expectedUnsavedDeal)).thenReturn(expectedSavedDeal);

        Deal savedDeal = dealService.save(unsavedDeal);

        verify(authenticationService).getCurrentUser();
        verifyNoMoreInteractions(authenticationService);

        verify(dealDao).save(expectedUnsavedDeal);
        verifyNoMoreInteractions(dealDao);

        assertReflectionEquals(expectedSavedDeal, savedDeal);
    }

    @Test(expectedExceptions = NoAuthenticatedUserException.class)
    public void testSave_NoAuthenticatedUser() throws Exception {
        when(authenticationService.getCurrentUser()).thenReturn(Optional.<User>absent());
        dealService.save(buildDeal(null, "admin1", "name1", false));
    }

    @Test(expectedExceptions = NotAuthorizedUserException.class)
    public void testSave_NotAuthorizedUserUser() throws Exception {
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(user));
        dealService.save(buildDeal(null, "admin1", "name1", true));
    }

    @Test(expectedExceptions = NoAuthenticatedUserException.class)
    public void testDelete_NotAuthenticated() throws Exception {
        when(authenticationService.getCurrentUser()).thenReturn(Optional.<User>absent());
        dealService.delete(1L);
    }

    @Test(expectedExceptions = NotAuthorizedUserException.class)
    public void testDelete_NotAuthorized() throws Exception {
        Deal deal = buildDeal(1L, "userId", "name1", false);

        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(user));
        when(dealDao.get(deal.getId())).thenReturn(deal);

        dealService.delete(deal.getId());
    }

    @Test
    public void testDelete() throws Exception {
        Deal deal = buildDeal(1L, user.getGoogleId(), "name1", false);

        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(user));
        when(dealDao.get(deal.getId())).thenReturn(deal);

        dealService.delete(deal.getId());

        verify(dealDao).delete(deal);
    }

    @Test
    public void testGet() throws Exception {
        Deal deal = buildDeal(1L, "admin1", "name1", true);
        when(dealDao.get(deal.getId())).thenReturn(deal);

        Deal actualDeal = dealService.get(deal.getId());

        verify(dealDao).get(deal.getId());
        verifyNoMoreInteractions(dealDao);

        assertReflectionEquals(deal, actualDeal);
    }

    @Test
    public void testGetCurrentUserOrSharedDeals_WithCurrentUser() throws Exception {
        List<Deal> userDeals = newArrayList(
            buildDeal(1L, "admin1", "name1", true),
            buildDeal(2L, "admin2", "name2", true),
            buildDeal(3L, "admin2", "name3", true)
        );

        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(user));
        when(dealDao.getUserDeals(user)).thenReturn(userDeals);

        List<Deal> deals = dealService.getCurrentUserOrSharedDeals();

        verify(authenticationService).getCurrentUser();
        verifyNoMoreInteractions(authenticationService);

        verify(dealDao).getUserDeals(user);
        verifyNoMoreInteractions(dealDao);

        assertReflectionEquals(userDeals, deals);
    }

    @Test
    public void testGetCurrentUserOrSharedDeals_NoCurrentUser() throws Exception {
        List<Deal> sharedDeals = newArrayList(
            buildDeal(1L, "admin1", "name1", true),
            buildDeal(2L, "admin2", "name2", true),
            buildDeal(3L, "admin2", "name3", true)
        );

        when(authenticationService.getCurrentUser()).thenReturn(Optional.<User>absent());
        when(dealDao.getSharedDeals()).thenReturn(sharedDeals);

        List<Deal> deals = dealService.getCurrentUserOrSharedDeals();

        verify(authenticationService).getCurrentUser();
        verifyNoMoreInteractions(authenticationService);

        verify(dealDao).getSharedDeals();
        verifyNoMoreInteractions(dealDao);

        assertReflectionEquals(sharedDeals, deals);
    }

    private Deal buildDeal(Long dealId, String userId, String name, boolean shared) {
        Deal deal = new Deal();
        deal.setId(dealId);
        deal.setShared(shared);
        deal.setUserId(userId);
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
