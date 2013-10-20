package com.preferanser.client.util;

public class EnumRotator<E extends Enum<E>> {

    private E[] values;
    private int currentOrdinal = 0;

    public EnumRotator(E[] values) {
        this.values = values;
    }

    public EnumRotator(E[] values, int start) {
        this.values = values;
        currentOrdinal = start;
    }

    public EnumRotator(E[] values, E startValue) {
        this(values, startValue.ordinal());
    }

    public void setStart(E value) {
        currentOrdinal = value.ordinal();
    }

    public E next() {
        if (currentOrdinal >= values.length) {
            currentOrdinal = 0;
        }
        return values[currentOrdinal++];
    }

}
