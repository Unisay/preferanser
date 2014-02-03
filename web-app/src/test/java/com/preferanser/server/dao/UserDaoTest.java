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

@Listeners({ClockTestNGListener.class, DatastoreTestNGListener.class})
public class UserDaoTest {

    private UserDao userDao;

    @BeforeMethod
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new DaoTestModule());
        userDao = injector.getInstance(UserDao.class);
    }

    @Test(expectedExceptions = com.googlecode.objectify.NotFoundException.class,
        expectedExceptionsMessageRegExp = "^No entity was found matching the key.*")
    public void testFindByGoogleId_NotFound() throws Exception {
        userDao.findById("123");
    }

    @Test
    public void testFindByGoogleId() throws Exception {
        User user = buildUser("googleId", true);
        userDao.save(user);

        User actualUser = userDao.findById("googleId");
        assertThat(actualUser, equalTo(user));
    }

}
