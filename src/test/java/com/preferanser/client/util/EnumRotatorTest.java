package com.preferanser.client.util;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class EnumRotatorTest {

    private EnumRotator<TestEnum> enumRotator;

    enum TestEnum {
        A, B, C, D
    }

    @Before
    public void setUp() throws Exception {
        enumRotator = new EnumRotator<TestEnum>(TestEnum.values());
    }

    @Test
    public void testStart() throws Exception {
        enumRotator.setStart(TestEnum.D);
        assertThat(enumRotator.next(), equalTo(TestEnum.D));
        assertThat(enumRotator.next(), equalTo(TestEnum.A));
    }

    @Test
    public void testNext() throws Exception {
        assertThat(enumRotator.next(), equalTo(TestEnum.A));
        assertThat(enumRotator.next(), equalTo(TestEnum.B));
        assertThat(enumRotator.next(), equalTo(TestEnum.C));
        assertThat(enumRotator.next(), equalTo(TestEnum.D));
        assertThat(enumRotator.next(), equalTo(TestEnum.A));
    }

}
