package com.preferanser.server.dao;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.entity.User;
import com.preferanser.testng.ClockTestNGListener;
import com.preferanser.testng.DatastoreTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

import static com.preferanser.server.dao.DaoTestHelper.buildDeal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.collections.Lists.newArrayList;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;

@Listeners({ClockTestNGListener.class, DatastoreTestNGListener.class})
public class DealDaoTest {

    private DealDao dealDao;

    @BeforeMethod
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new DaoTestModule());
        dealDao = injector.getInstance(DealDao.class);
    }

    @Test
    public void testGetAll() throws Exception {
        assertThat(dealDao.getAll(), empty());
    }

    @Test
    public void testSave() throws Exception {
        Deal savedDeal = dealDao.save(buildDeal("name", new Date(1)));
        assertThat(savedDeal.getId(), is(not(nullValue())));
        assertReflectionEquals(buildDeal("name", new Date(1)), savedDeal, IGNORE_DEFAULTS);
    }

    @Test
    public void testGetAllDescDateCreated() throws Exception {
        Deal deal1 = buildDeal("deal1", new Date(1000));
        Deal deal2 = buildDeal("deal2", new Date(2000));
        Deal deal3 = buildDeal("deal3", new Date(3000));

        dealDao.save(deal1, deal2, deal3);

        List<Deal> actualDeals = dealDao.getAllDescDateCreated();
        List<Deal> expectedDeals = newArrayList(deal3, deal2, deal1);
        assertReflectionEquals(expectedDeals, actualDeals);
    }

    @Test
    public void testGetSharedDeals() throws Exception {
        Deal deal1 = buildDeal("deal1", new Date(1000));
        Deal deal2 = buildDeal("deal2", new Date(2000));
        Deal deal3 = buildDeal("deal3", new Date(3000));

        deal1.setShared(true);
        deal2.setShared(true);

        dealDao.save(deal1, deal2, deal3);

        List<Deal> actualDeals = dealDao.getSharedDeals();
        List<Deal> expectedDeals = newArrayList(deal2, deal1);
        assertReflectionEquals(expectedDeals, actualDeals);
    }

    @Test
    public void testGetUserDeals() throws Exception {
        Deal deal1 = buildDeal("deal1", new Date(1000));
        Deal deal2 = buildDeal("deal2", new Date(2000));
        Deal deal3 = buildDeal("deal3", new Date(3000));
        deal3.setUserId("otherUser");

        User user = new User();
        user.setGoogleId(deal1.getUserId());

        deal1.setShared(true);
        deal2.setShared(true);

        dealDao.save(deal1, deal2, deal3);

        List<Deal> actualDeals = dealDao.getUserDeals(user);
        List<Deal> expectedDeals = newArrayList(deal2, deal1);
        assertReflectionEquals(expectedDeals, actualDeals);
    }
}
