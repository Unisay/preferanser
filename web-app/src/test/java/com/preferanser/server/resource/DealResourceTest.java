package com.preferanser.server.resource;

import com.preferanser.testng.ClockTestNGListener;
import org.testng.annotations.Listeners;

@Listeners(ClockTestNGListener.class)
public class DealResourceTest {
    // TODO: Fix
  /*  private DealResource dealResource;

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

        dealResource = new DealResource(dealDao);
    }

    @Test
    public void testGetAllSharedDeals() throws Exception {
        when(dealDao.getSharedDeals()).thenReturn(dealList);

        assertReflectionEquals(dealList, dealResource.getSharedDeals());

        verify(dealDao).getSharedDeals();
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
    }*/
}
