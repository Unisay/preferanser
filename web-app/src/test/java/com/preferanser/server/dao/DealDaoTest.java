package com.preferanser.server.dao;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.testng.ClockTestNGListener;
import com.preferanser.testng.DatastoreTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

import static com.preferanser.server.dao.DaoTestHelper.buildDealEntity;
import static com.preferanser.server.dao.DaoTestHelper.buildUser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.collections.Lists.newArrayList;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;

@Listeners({ClockTestNGListener.class, DatastoreTestNGListener.class})
public class DealDaoTest {

    private DealDao dealDao;
    private UserEntity user;

    @BeforeMethod
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new DaoTestModule());
        dealDao = injector.getInstance(DealDao.class);
        user = DaoTestHelper.buildUser(123L, true);
    }

    @Test
    public void testGetAll() throws Exception {
        assertThat(dealDao.getAll(), empty());
    }

    @Test
    public void testSave() throws Exception {
        DealEntity savedDeal = dealDao.save(buildDealEntity("name", new Date(1), user));
        assertThat(savedDeal.getId(), is(not(nullValue())));
        assertReflectionEquals(buildDealEntity("name", new Date(1), user), savedDeal, IGNORE_DEFAULTS);
    }

    @Test
    public void testGetAllDescDateCreated() throws Exception {
        DealEntity deal1 = buildDealEntity("deal1", new Date(1000), user);
        DealEntity deal2 = buildDealEntity("deal2", new Date(2000), user);
        DealEntity deal3 = buildDealEntity("deal3", new Date(3000), user);

        dealDao.save(deal1, deal2, deal3);

        List<DealEntity> actualDeals = dealDao.getAllDescDateCreated();
        List<DealEntity> expectedDeals = newArrayList(deal3, deal2, deal1);
        assertReflectionEquals(expectedDeals, actualDeals);
    }

    @Test
    public void testGetSharedDeals() throws Exception {
        DealEntity deal1 = buildDealEntity("deal1", new Date(1000), user);
        DealEntity deal2 = buildDealEntity("deal2", new Date(2000), user);
        DealEntity deal3 = buildDealEntity("deal3", new Date(3000), user);

        deal1.setShared(true);
        deal2.setShared(true);

        dealDao.save(deal1, deal2, deal3);

        List<DealEntity> actualDeals = dealDao.getSharedDeals();
        List<DealEntity> expectedDeals = newArrayList(deal2, deal1);
        assertReflectionEquals(expectedDeals, actualDeals);
    }

    @Test
    public void testGetUserDeals() throws Exception {
        DealEntity deal1 = buildDealEntity("deal1", new Date(1000), user);
        DealEntity deal2 = buildDealEntity("deal2", new Date(2000), user);
        DealEntity deal3 = buildDealEntity("deal3", new Date(3000), buildUser(234, false));

        dealDao.save(deal1, deal2, deal3);

        List<DealEntity> actualDeals = dealDao.getUserDeals(user);
        List<DealEntity> expectedDeals = newArrayList(deal2, deal1);
        assertReflectionEquals(expectedDeals, newArrayList(actualDeals)); // newArray to unwrap Proxy<?>
    }

    @Test
    public void testDeleteNow() throws Exception {
        DealEntity deal = buildDealEntity("deal1", new Date(1000), user);
        DealEntity savedDeal = dealDao.save(deal);
        assertTrue(dealDao.find(user, savedDeal.getId()).isPresent(), "Saved DealEntity is not present in the DB");
        dealDao.deleteNow(savedDeal);
        assertFalse(dealDao.find(user, savedDeal.getId()).isPresent(), "Deleted DealEntity is still present in the DB");
    }

}
