package com.preferanser.server.resource;

import com.preferanser.server.service.AuthenticationService;
import com.preferanser.shared.domain.User;
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
    private User user;

    @Mock
    private AuthenticationService authenticationService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        user = new User(true, true, "logout", "login", "email", "nickname");
        authResource = new AuthResource(authenticationService);
    }

    @Test
    public void testGetCurrentUserInfo() throws Exception {
        when(authenticationService.get()).thenReturn(user);
        User currentUserInfo = authResource.getCurrentUserInfo();
        assertThat(currentUserInfo, equalTo(user));
        verify(authenticationService).get();
    }

}
