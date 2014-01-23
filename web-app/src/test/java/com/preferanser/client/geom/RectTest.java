package com.preferanser.client.geom;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class RectTest {

    private Rect rect;

    @BeforeMethod
    public void setUp() throws Exception {
        Point leftTop = new Point(10, 20);
        Point rightBottom = new Point(100, 200);
        rect = new Rect(leftTop, rightBottom);
    }

    @Test
    public void testContains() throws Exception {
        assertTrue(rect.contains(new Point(10, 20)));
        assertTrue(rect.contains(new Point(100, 200)));
        assertTrue(rect.contains(new Point(11, 100)));
        assertTrue(rect.contains(new Point(100, 100)));
        assertFalse(rect.contains(new Point(9, 19)));
        assertFalse(rect.contains(new Point(101, 201)));
    }

    @Test
    public void testCenter() throws Exception {
        assertThat(rect.center(), equalTo(new Point(55, 110)));
    }
}
