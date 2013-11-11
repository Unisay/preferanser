package com.preferanser.util;

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
