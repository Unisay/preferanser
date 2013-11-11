package com.preferanser.util;

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
    public void testCurrent() throws Exception {
        enumRotator.setCurrent(TestEnum.D);
        assertThat(enumRotator.current(), equalTo(TestEnum.D));
        assertThat(enumRotator.next(), equalTo(TestEnum.A));
        assertThat(enumRotator.current(), equalTo(TestEnum.A));
        assertThat(enumRotator.next(), equalTo(TestEnum.B));
        assertThat(enumRotator.current(), equalTo(TestEnum.B));
    }

    @Test
    public void testNext() throws Exception {
        assertThat(enumRotator.next(), equalTo(TestEnum.A));
        assertThat(enumRotator.next(), equalTo(TestEnum.B));
        assertThat(enumRotator.next(), equalTo(TestEnum.C));
        assertThat(enumRotator.next(), equalTo(TestEnum.D));
        assertThat(enumRotator.next(), equalTo(TestEnum.A));
    }

    @Test
    public void testNextAfterConstructor() throws Exception {
        assertThat(new EnumRotator<TestEnum>(TestEnum.values(), TestEnum.A).next(), equalTo(TestEnum.B));
    }

    @Test
    public void testSkippingValues() throws Exception {
        enumRotator.setSkipValues(TestEnum.B, TestEnum.C);
        assertThat(enumRotator.next(), equalTo(TestEnum.A));
        assertThat(enumRotator.next(), equalTo(TestEnum.D));
        assertThat(enumRotator.next(), equalTo(TestEnum.A));
        assertThat(enumRotator.next(), equalTo(TestEnum.D));
    }
}
