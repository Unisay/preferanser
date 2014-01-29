package com.preferanser.server.resource;

import com.google.common.base.Optional;
import com.preferanser.server.dao.DealDao;
import com.preferanser.server.exception.NoAuthenticatedUserException;
import com.preferanser.server.exception.NotAuthorizedUserException;
import com.preferanser.server.service.AuthenticationService;
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

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@Listeners(ClockTestNGListener.class)
public class DealResourceTest {

    private DealResource dealResource;

    @Mock
    private DealDao dealDao;

    @Mock
    private AuthenticationService authenticationService;
    private List<Deal> dealList;
    private User user;
    private Deal userDeal;
    private Deal otherUserDeal;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        user = buildUser(1L, "googleId");

        userDeal = new Deal();
        userDeal.setId(1L);
        userDeal.setUserId(user.getGoogleId());

        otherUserDeal = new Deal();
        otherUserDeal.setId(2L);
        otherUserDeal.setUserId("otherUserGoogleId");

        dealList = newArrayList(userDeal, otherUserDeal);

        dealResource = new DealResource(authenticationService, dealDao);
    }

    @Test
    public void testGetAllSharedDeals() throws Exception {
        when(dealDao.getAllSharedDeals()).thenReturn(dealList);

        assertReflectionEquals(dealList, dealResource.getAllSharedDeals());

        verify(dealDao).getAllSharedDeals();
    }

    @Test
    public void testGetById() throws Exception {
        when(dealDao.get(userDeal.getId())).thenReturn(userDeal);

        Deal actualDeal = dealResource.getById(userDeal.getId());
        assertThat(actualDeal, equalTo(userDeal));

        verify(dealDao).get(userDeal.getId());
    }

    @Test(expectedExceptions = NoAuthenticatedUserException.class)
    public void testSave_NoAuthenticatedUser() throws Exception {
        when(authenticationService.getCurrentUser()).thenReturn(Optional.<User>absent());
        dealResource.save(userDeal);
    }

    @Test(expectedExceptions = NotAuthorizedUserException.class)
    public void testSave_NotAuthorizedUserUser() throws Exception {
        userDeal.setShared(true);
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(user));

        dealResource.save(userDeal);
    }

    @Test
    public void testSave_SharedByAdmin() throws Exception {
        Deal newDeal = new Deal();
        newDeal.setId(333L);
        newDeal.setUserId("otherUserId");
        newDeal.setShared(true);

        Deal dealToSave = new Deal(newDeal);
        dealToSave.setId(null);
        dealToSave.setUserId(user.getGoogleId());
        dealToSave.setCreated(Clock.getNow());

        user.setAdmin(true);

        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(user));
        when(dealDao.save(dealToSave)).thenReturn(userDeal);

        Long dealId = dealResource.save(newDeal);
        assertThat(dealId, equalTo(userDeal.getId()));

        verify(authenticationService).getCurrentUser();
        verify(dealDao).save(dealToSave);
    }

    @Test
    public void testSave() throws Exception {
        Deal newDeal = new Deal();
        newDeal.setId(1L);
        newDeal.setUserId("otherUserId");

        Deal dealToSave = new Deal(newDeal);
        dealToSave.setId(null);
        dealToSave.setUserId(user.getGoogleId());
        dealToSave.setCreated(Clock.getNow());

        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(user));
        when(dealDao.save(dealToSave)).thenReturn(userDeal);

        Long dealId = dealResource.save(newDeal);
        assertThat(dealId, equalTo(userDeal.getId()));

        verify(authenticationService).getCurrentUser();
        verify(dealDao).save(dealToSave);
    }

    @Test(expectedExceptions = NoAuthenticatedUserException.class)
    public void testDelete_NoAuthenticatedUser() throws Exception {
        when(authenticationService.getCurrentUser()).thenReturn(Optional.<User>absent());
        dealResource.delete(userDeal.getId());
    }

    @Test(expectedExceptions = NotAuthorizedUserException.class)
    public void testDelete_NotAuthorizedUserUser() throws Exception {
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(user));
        when(dealDao.get(otherUserDeal.getId())).thenReturn(otherUserDeal);

        dealResource.delete(otherUserDeal.getId());
    }

    @Test
    public void testDelete() throws Exception {
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(user));
        when(dealDao.get(userDeal.getId())).thenReturn(userDeal);

        dealResource.delete(userDeal.getId());

        verify(dealDao).delete(userDeal);
    }

    private static User buildUser(long id, String googleId) {
        User user = new User();
        user.setId(id);
        user.setGoogleId(googleId);
        return user;
    }
}
