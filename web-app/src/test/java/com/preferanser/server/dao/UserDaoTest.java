package com.preferanser.server.dao;

import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.testng.ClockTestNGListener;
import com.preferanser.testng.DatastoreTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.preferanser.server.dao.DaoTestHelper.buildUser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Listeners({ClockTestNGListener.class, DatastoreTestNGListener.class})
public class UserDaoTest {

    private UserDao userDao;

    @BeforeMethod
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new DaoTestModule());
        userDao = injector.getInstance(UserDao.class);
    }

    @Test
    public void testFindByGoogleId_NotFound() throws Exception {
        assertFalse(userDao.findByGoogleId("nonExistingGoogleId").isPresent());
    }

    @Test
    public void testFindByGoogleId() throws Exception {
        UserEntity user = buildUser(123L, true);
        userDao.save(user);

        Optional<UserEntity> userEntityOptional = userDao.findByGoogleId(user.getGoogleId());
        assertTrue(userEntityOptional.isPresent());
        assertThat(userEntityOptional.get(), equalTo(user));
    }

}
