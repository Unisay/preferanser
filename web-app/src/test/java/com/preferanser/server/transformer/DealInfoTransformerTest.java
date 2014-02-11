package com.preferanser.server.transformer;

import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.shared.domain.DealInfo;
import com.preferanser.shared.util.Clock;
import com.preferanser.testng.ClockTestNGListener;
import com.preferanser.testng.DatastoreTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@Listeners({ClockTestNGListener.class, DatastoreTestNGListener.class})
public class DealInfoTransformerTest {

    private DealInfoTransformer dealInfoTransformer;

    @BeforeMethod
    public void setUp() throws Exception {
        dealInfoTransformer = new DealInfoTransformer();
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testToEntity() throws Exception {
        dealInfoTransformer.toEntity(buildDealInfo());
    }

    @Test
    public void testFromEntity() throws Exception {
        DealInfo dealInfo = dealInfoTransformer.fromEntity(buildDealEntity());
        assertReflectionEquals(buildDealInfo(), dealInfo);
    }

    private DealEntity buildDealEntity() {
        UserEntity owner = new UserEntity();
        owner.setId(999L);

        DealEntity entity = new DealEntity();
        entity.setId(1L);
        entity.setName("name");
        entity.setDescription("description");
        entity.setOwner(owner);
        entity.setCreated(Clock.getNow());
        return entity;
    }

    private DealInfo buildDealInfo() {
        return new DealInfo(1L, "name", "description", 999L, Clock.getNow());
    }
}
