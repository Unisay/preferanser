package com.preferanser.shared.domain;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class UserTest {

    private User user;

    @BeforeMethod
    public void setUp() throws Exception {
        user = buildUser();
    }

    @Test
    public void testCopyFrom() throws Exception {
        User copy = new User().copyFrom(user);
        assertReflectionEquals(user, copy);
    }

    @Test
    public void testEquals_Positive() throws Exception {
        assertThat(user, equalTo(buildUser()));
    }

    @Test(dataProvider = "user-properties")
    public void testEquals_Negative(
        boolean admin,
        String email,
        boolean loggedIn,
        String loginUrl,
        String logoutUrl,
        String nickname
    ) throws Exception {
        User user = buildUser();
        user.setAdmin(admin);
        user.setEmail(email);
        user.setLoggedIn(loggedIn);
        user.setLoginUrl(loginUrl);
        user.setLogoutUrl(logoutUrl);
        user.setNickname(nickname);

        assertThat(user, not(equalTo(buildUser())));
    }

    @DataProvider(name = "user-properties")
    public Object[][] data() {
        return new Object[][]{
            {false, "user@email.com", true, "loginUrl", "logoutUrl", "nickname"},
            {true, "user_other@email.com", true, "loginUrl", "logoutUrl", "nickname"},
            {true, "user@email.com", false, "loginUrl", "logoutUrl", "nickname"},
            {true, "user@email.com", true, "loginUrl_other", "logoutUrl", "nickname"},
            {true, "user@email.com", true, "loginUrl", "logoutUrl_other", "nickname"},
            {true, "user@email.com", true, "loginUrl", "logoutUrl", "nickname_other"}
        };
    }

    private User buildUser() {
        User result = new User();
        result.setAdmin(true);
        result.setEmail("user@email.com");
        result.setLoggedIn(true);
        result.setLoginUrl("loginUrl");
        result.setLogoutUrl("logoutUrl");
        result.setNickname("nickname");
        return result;
    }
}
