package com.preferanser.server.service;

import com.google.appengine.api.users.UserService;
import com.google.common.base.Optional;
import com.google.inject.Provider;
import com.preferanser.server.dao.UserDao;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.server.exception.NoAuthenticatedUserException;
import com.preferanser.shared.domain.User;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    @Mock
    private UserDao userDao;

    @Mock
    private UserService userService;

    @Mock
    private DealService dealService;

    @Mock
    private Provider<UserService> userServiceProvider;

    private UserEntity currentUser;
    private com.google.appengine.api.users.User currentGoogleUser;
    private User user;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(userServiceProvider.get()).thenReturn(userService);
        authenticationService = new AuthenticationService(userServiceProvider, dealService, userDao);

        currentGoogleUser = new com.google.appengine.api.users.User("user@gmail.com", "authDomain", "googleId");

        currentUser = new UserEntity();
        currentUser.setId(currentGoogleUser.getUserId());
        currentUser.setEmail(currentGoogleUser.getEmail());
        currentUser.setAdmin(true);

        user = new User(true, true, "/logout", "/login", currentGoogleUser.getEmail(), currentGoogleUser.getNickname());

        when(userService.getCurrentUser()).thenReturn(currentGoogleUser);
    }

    @Test
    public void testGet() throws Exception {
        when(userDao.save(Matchers.any(UserEntity.class))).thenAnswer(new UserDaoSaveAnswer());
        when(userService.isUserLoggedIn()).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(currentGoogleUser);
        when(userService.isUserAdmin()).thenReturn(true);
        when(userService.createLoginURL("/")).thenReturn("/login");
        when(userService.createLogoutURL("/")).thenReturn("/logout");
        when(userDao.findById(currentGoogleUser.getUserId())).thenReturn(Optional.<UserEntity>absent());

        User actualUser = authenticationService.get();
        assertReflectionEquals(user, actualUser);

        verify(userDao).save(Matchers.any(UserEntity.class));
        verify(userService, atLeastOnce()).isUserLoggedIn();
        verify(userService, atLeastOnce()).getCurrentUser();
        verify(userService, atLeastOnce()).isUserAdmin();
        verify(userService).createLoginURL("/");
        verify(userService).createLogoutURL("/");
    }

    @Test
    public void testGet_NotPresent() throws Exception {
        when(userService.isUserAdmin()).thenReturn(true);
        when(userService.createLoginURL("/")).thenReturn("/login");
        when(userService.createLogoutURL("/")).thenReturn("/logout");
        when(userService.isUserLoggedIn()).thenReturn(false);

        User expectedUser = new User(false, false, "/logout", "/login", null, null);
        User actualUser = authenticationService.get();
        assertReflectionEquals(expectedUser, actualUser);
    }

    @Test
    public void testGetCurrentUser_FirstTime() throws Exception {
        when(userService.isUserLoggedIn()).thenReturn(true);
        when(userService.isUserAdmin()).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(currentGoogleUser);
        when(userDao.findById(currentGoogleUser.getUserId())).thenReturn(Optional.<UserEntity>absent());
        when(userDao.save(Matchers.any(UserEntity.class))).thenAnswer(new UserDaoSaveAnswer());

        Optional<UserEntity> maybeCurrentUser = authenticationService.getCurrentUser();
        assertTrue(maybeCurrentUser.isPresent(),
            "authenticationService.getCurrentUser() returned Optional.absent() when Object was expected");
        assertReflectionEquals(currentUser, maybeCurrentUser.get());

        verify(userDao).findById(currentGoogleUser.getUserId());
        verify(userDao).save(Matchers.any(UserEntity.class));
        verifyNoMoreInteractions(userDao);

        verify(dealService).importSharedDeals(Matchers.any(UserEntity.class));
        verifyNoMoreInteractions(dealService);
    }

    @Test
    public void testGetCurrentUser_NextTime() throws Exception {
        when(userService.isUserLoggedIn()).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(currentGoogleUser);
        when(userDao.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));

        Optional<UserEntity> maybeCurrentUser = authenticationService.getCurrentUser();
        assertTrue(maybeCurrentUser.isPresent(),
            "authenticationService.getCurrentUser() returned Optional.absent() when Object was expected");
        assertReflectionEquals(currentUser, maybeCurrentUser.get());

        verify(userDao).findById(currentUser.getId());
        verifyNoMoreInteractions(userDao);
        verifyNoMoreInteractions(dealService);
    }

    @Test
    public void testGetCurrentUser_NotPresent() throws Exception {
        when(userService.isUserLoggedIn()).thenReturn(false);

        Optional<UserEntity> maybeCurrentUser = authenticationService.getCurrentUser();
        assertFalse(maybeCurrentUser.isPresent(),
            "authenticationService.getCurrentUser() returned Object when Optional.absent() was expected");
    }

    @Test
    public void testGetCurrentUserOrThrow_LoggedIn() throws Exception {
        when(userService.isUserLoggedIn()).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(currentGoogleUser);
        when(userDao.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));

        UserEntity userEntity = authenticationService.getCurrentUserOrThrow();

        assertReflectionEquals(currentUser, userEntity);

        verify(userDao).findById(currentUser.getId());
        verifyNoMoreInteractions(userDao);
        verifyNoMoreInteractions(dealService);
    }

    @Test(expectedExceptions = NoAuthenticatedUserException.class)
    public void testGetCurrentUserOrThrow_NotLoggedIn() throws Exception {
        when(userService.isUserLoggedIn()).thenReturn(false);
        authenticationService.getCurrentUserOrThrow();
    }

    private class UserDaoSaveAnswer implements Answer<UserEntity> {
        @Override public UserEntity answer(InvocationOnMock invocation) throws Throwable {
            return (UserEntity) invocation.getArguments()[0];
        }
    }
}
