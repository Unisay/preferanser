package com.preferanser.server.transformer;

import com.preferanser.testng.ClockTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.preferanser.shared.domain.entity.DealTestHelper.buildDeal;
import static com.preferanser.shared.domain.entity.DealTestHelper.buildDealEntity;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@Listeners({ClockTestNGListener.class})
public class DealTransformerTest {

    private DealTransformer transformer;

    @BeforeMethod
    public void setUp() throws Exception {
        transformer = new DealTransformer();
    }

    @Test
    public void testToEntity() throws Exception {
        assertReflectionEquals(buildDealEntity(), transformer.toEntity(buildDeal()));
    }

    @Test
    public void testFromEntity() throws Exception {
        assertReflectionEquals(buildDeal(), transformer.fromEntity(buildDealEntity()));
    }

}
