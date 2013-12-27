/*
 * Preferanser is a program to simulate and calculate Russian Preferans Card game deals.
 *
 *     Copyright (C) 2013  Yuriy Lazarev <Yuriy.Lazarev@gmail.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see [http://www.gnu.org/licenses/].
 */

package com.preferanser.shared.domain;

import com.preferanser.shared.util.EnumRotator;
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
