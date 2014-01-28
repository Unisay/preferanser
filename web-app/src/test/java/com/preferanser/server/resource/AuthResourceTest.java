package com.preferanser.server.resource;

import com.preferanser.server.service.AuthenticationService;
import com.preferanser.shared.domain.entity.User;
import com.preferanser.shared.dto.CurrentUserDto;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthResourceTest {

    private AuthResource authResource;
    private CurrentUserDto currentUserDto;

    @Mock
    private AuthenticationService authenticationService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        User currentUser = new User();
        currentUser.setGoogleId("googleId");
        currentUser.setAdmin(true);

        currentUserDto = new CurrentUserDto();
        currentUserDto.setAdmin(true);
        currentUserDto.setLoggedIn(true);
        currentUserDto.setLoginUrl("login");
        currentUserDto.setLogoutUrl("logout");
        currentUserDto.setNickname("nickname");
        currentUserDto.setUser(currentUser);

        authResource = new AuthResource(authenticationService);
    }

    @Test
    public void testGetCurrentUserInfo() throws Exception {
        when(authenticationService.get()).thenReturn(currentUserDto);
        CurrentUserDto currentUserInfo = authResource.getCurrentUserInfo();
        assertThat(currentUserInfo, equalTo(currentUserDto));
        verify(authenticationService).get();
    }

}
