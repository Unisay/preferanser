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

package com.preferanser.shared.util;

import com.google.common.base.Preconditions;

public class EnumRotator<E extends Enum<E>> {

    private E[] values;
    private int currentOrdinal;
    private E[] valuesToSkip;

    public EnumRotator(E[] values) {
        this.values = values;
        this.currentOrdinal = values.length - 1;
    }

    public EnumRotator(E[] values, E startValue) {
        Preconditions.checkNotNull(values, "EnumRotator: values are null");
        Preconditions.checkNotNull(values, "EnumRotator: current value is null");
        this.values = values;
        currentOrdinal = startValue.ordinal();
    }

    public void setCurrent(E value) {
        currentOrdinal = value.ordinal();
    }

    public void setSkipValues(E... valuesToSkip) {
        if (valuesToSkip.length >= values.length) {
            throw new IllegalArgumentException("Can't skip all values!");
        }
        this.valuesToSkip = valuesToSkip;
    }

    public E current() {
        return values[currentOrdinal];
    }

    public E next() {
        currentOrdinal = currentOrdinal < values.length - 1 ? currentOrdinal + 1 : 0;
        E result = values[currentOrdinal];

        if (valuesToSkip != null)
            for (E valueToSkip : valuesToSkip)
                if (valueToSkip == result)
                    return next();

        return result;
    }

}