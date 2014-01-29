package com.preferanser.server.service;

import com.google.appengine.api.users.UserService;
import com.google.common.base.Optional;
import com.google.inject.Provider;
import com.preferanser.server.dao.UserDao;
import com.preferanser.shared.domain.entity.User;
import com.preferanser.shared.dto.CurrentUserDto;
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
    private Provider<UserService> userServiceProvider;

    private User currentUser;
    private Long newUserId;
    private com.google.appengine.api.users.User currentGoogleUser;
    private CurrentUserDto currentUserDto;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(userServiceProvider.get()).thenReturn(userService);
        authenticationService = new AuthenticationService(userServiceProvider, userDao);

        newUserId = 1234L;

        currentGoogleUser = new com.google.appengine.api.users.User("user@gmail.com", "authDomain", "googleId");

        currentUser = new User();
        currentUser.setGoogleId(currentGoogleUser.getUserId());
        currentUser.setEmail(currentGoogleUser.getEmail());
        currentUser.setAdmin(true);

        currentUserDto = new CurrentUserDto();
        currentUserDto.setAdmin(true);
        currentUserDto.setLoggedIn(true);
        currentUserDto.setLoginUrl("/login");
        currentUserDto.setLogoutUrl("/logout");
        currentUserDto.setNickname(currentGoogleUser.getNickname());
        currentUserDto.setUser(currentUser);

        when(userService.getCurrentUser()).thenReturn(currentGoogleUser);
    }

    @Test
    public void testGet() throws Exception {
        currentUser.setId(newUserId);

        when(userDao.save(Matchers.any(User.class))).thenAnswer(new UserDaoSaveAnswer());
        when(userService.isUserLoggedIn()).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(currentGoogleUser);
        when(userService.isUserAdmin()).thenReturn(true);
        when(userService.createLoginURL("/")).thenReturn("/login");
        when(userService.createLogoutURL("/")).thenReturn("/logout");

        CurrentUserDto actualCurrentUserDto = authenticationService.get();
        assertReflectionEquals(currentUserDto, actualCurrentUserDto);
    }

    @Test
    public void testGetCurrentUser_FirstTime() throws Exception {
        currentUser.setId(newUserId);

        when(userService.isUserLoggedIn()).thenReturn(true);
        when(userService.isUserAdmin()).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(currentGoogleUser);
        when(userDao.findByGoogleId(currentUser.getGoogleId())).thenReturn(null);
        when(userDao.save(Matchers.any(User.class))).thenAnswer(new UserDaoSaveAnswer());

        Optional<User> maybeCurrentUser = authenticationService.getCurrentUser();
        assertTrue(maybeCurrentUser.isPresent(),
            "authenticationService.getCurrentUser() returned Optional.absent() when Object was expected");
        assertReflectionEquals(currentUser, maybeCurrentUser.get());

        verify(userDao).findByGoogleId(currentUser.getGoogleId());
        verify(userDao).save(Matchers.any(User.class));
        verifyNoMoreInteractions(userDao);
    }

    @Test
    public void testGetCurrentUser_NextTime() throws Exception {
        when(userService.isUserLoggedIn()).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(currentGoogleUser);
        when(userDao.findByGoogleId(currentUser.getGoogleId())).thenReturn(currentUser);

        Optional<User> maybeCurrentUser = authenticationService.getCurrentUser();
        assertTrue(maybeCurrentUser.isPresent(),
            "authenticationService.getCurrentUser() returned Optional.absent() when Object was expected");
        assertReflectionEquals(currentUser, maybeCurrentUser.get());

        verify(userDao).findByGoogleId(currentUser.getGoogleId());
        verifyNoMoreInteractions(userDao);
    }

    @Test
    public void testGetCurrentUser_NotPresent() throws Exception {
        when(userService.isUserLoggedIn()).thenReturn(false);

        Optional<User> maybeCurrentUser = authenticationService.getCurrentUser();
        assertFalse(maybeCurrentUser.isPresent(),
            "authenticationService.getCurrentUser() returned Object when Optional.absent() was expected");
    }

    private class UserDaoSaveAnswer implements Answer<User> {
        @Override public User answer(InvocationOnMock invocation) throws Throwable {
            User user = (User) invocation.getArguments()[0];
            user.setId(newUserId);
            return user;
        }
    }
}
