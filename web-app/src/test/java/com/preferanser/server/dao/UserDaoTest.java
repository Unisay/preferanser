package com.preferanser.server.dao;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.preferanser.shared.domain.entity.User;
import com.preferanser.testng.ClockTestNGListener;
import com.preferanser.testng.DatastoreTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.preferanser.server.dao.DaoTestHelper.buildUser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertNull;

@Listeners({ClockTestNGListener.class, DatastoreTestNGListener.class})
public class UserDaoTest {

    private UserDao userDao;

    @BeforeMethod
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new DaoTestModule());
        userDao = injector.getInstance(UserDao.class);
    }

    @Test
    public void testFindByGoogleId_Null() throws Exception {
        User user = userDao.findByGoogleId("googleId");
        assertNull(user);
    }

    @Test
    public void testFindByGoogleId() throws Exception {
        User user = buildUser("googleId", true);
        userDao.save(user);

        User actualUser = userDao.findByGoogleId("googleId");
        assertThat(actualUser, equalTo(user));
    }

}
