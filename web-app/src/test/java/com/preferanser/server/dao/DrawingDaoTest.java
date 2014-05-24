package com.preferanser.server.dao;

import com.google.inject.Guice;
import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.entity.DrawingEntity;
import com.preferanser.testng.ClockTestNGListener;
import com.preferanser.testng.DatastoreTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static com.preferanser.server.dao.DaoTestHelper.buildDealEntity;
import static com.preferanser.server.dao.DaoTestHelper.buildDrawingEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.collections.Lists.newArrayList;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;

@Listeners({ClockTestNGListener.class, DatastoreTestNGListener.class})
public class DrawingDaoTest {

    private DrawingDao drawingDao;

    @BeforeMethod
    public void setUp() throws Exception {
        drawingDao = Guice.createInjector(new DaoTestModule()).getInstance(DrawingDao.class);
    }

    @Test
    public void testSave() throws Exception {
        DealEntity dealEntity = buildDealEntity(100L);
        DrawingEntity unsavedDrawing = buildDrawingEntity(dealEntity);
        DrawingEntity expectedSavedDrawing = buildDrawingEntity(dealEntity);

        DrawingEntity actualSavedDrawing = drawingDao.save(unsavedDrawing);

        assertThat(actualSavedDrawing.getId(), is(not(nullValue())));
        assertReflectionEquals(expectedSavedDrawing, actualSavedDrawing, IGNORE_DEFAULTS);
        assertTrue(drawingDao.get(dealEntity, actualSavedDrawing.getId()).isPresent(), "Saved DrawingEntity is not present in the DB");
    }

    @Test
    public void testDeleteNow() throws Exception {
        DealEntity dealEntity = buildDealEntity(100L);
        DrawingEntity unsavedDrawing = buildDrawingEntity(dealEntity);

        DrawingEntity actualSavedDrawing = drawingDao.save(unsavedDrawing);

        assertTrue(drawingDao.get(dealEntity, actualSavedDrawing.getId()).isPresent(), "Saved DrawingEntity is not present in the DB");
        drawingDao.deleteNow(actualSavedDrawing);
        assertFalse(drawingDao.get(dealEntity, actualSavedDrawing.getId()).isPresent(), "Deleted DealEntity is still present in the DB");
    }

    @Test
    public void testGetAllByDeal() throws Exception {
        DealEntity dealEntity = buildDealEntity(100L);
        DrawingEntity unsavedDrawing1 = buildDrawingEntity(dealEntity);
        DrawingEntity unsavedDrawing2 = buildDrawingEntity(dealEntity);
        drawingDao.save(unsavedDrawing1, unsavedDrawing2);

        List<DrawingEntity> actualDrawings = drawingDao.getAllDescDateCreated(dealEntity);
        List<DrawingEntity> expectedDrawings = newArrayList(unsavedDrawing1, unsavedDrawing2);
        assertReflectionEquals(expectedDrawings, actualDrawings);
    }
}