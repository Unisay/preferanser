package com.preferanser.server.transformer;

import com.preferanser.server.entity.DrawingEntity;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.Drawing;
import com.preferanser.shared.util.Clock;
import com.preferanser.testng.ClockTestNGListener;
import com.preferanser.testng.DatastoreTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.collections.Lists.newArrayList;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@Listeners({ClockTestNGListener.class, DatastoreTestNGListener.class})
public class DrawingTransformerTest {

    private DrawingTransformer transformer;

    @BeforeMethod
    public void setUp() throws Exception {
        transformer = new DrawingTransformer();
    }

    @Test
    public void testToEntity() throws Exception {
        assertReflectionEquals(buildDrawingEntity(), transformer.toEntity(buildDrawing()));
    }

    @Test
    public void testFromEntity() throws Exception {
        assertReflectionEquals(buildDrawing(), transformer.fromEntity(buildDrawingEntity()));
    }

    private Drawing buildDrawing() {
        return new Drawing(1L, 2L, 3L, "name", "desc", newArrayList(Card.CLUB_10, Card.CLUB_7), Clock.getNow());
    }

    private DrawingEntity buildDrawingEntity() {
        DrawingEntity drawingEntity = new DrawingEntity();
        drawingEntity.setId(1L);
        drawingEntity.setDeal(2L, 3L);
        drawingEntity.setName("name");
        drawingEntity.setDescription("desc");
        drawingEntity.setTurns(newArrayList(Card.CLUB_10, Card.CLUB_7));
        drawingEntity.setCreated(Clock.getNow());
        return drawingEntity;
    }

}
