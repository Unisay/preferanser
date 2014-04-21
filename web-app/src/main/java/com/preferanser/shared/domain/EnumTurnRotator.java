package com.preferanser.shared.domain;

import com.preferanser.shared.util.EnumRotator;

/**
 * Turn rotator based on enum rotator
 */
public class EnumTurnRotator implements TurnRotator {

    private EnumRotator<Hand> handEnumRotator;

    public EnumTurnRotator(EnumRotator<Hand> handEnumRotator) {
        this.handEnumRotator = handEnumRotator;
    }

    @Override public Hand current() {
        return handEnumRotator.current();
    }

    @Override public void next() {
        handEnumRotator.next();
    }

    @Override public void prev() {
        handEnumRotator.prev();
    }

    @Override public TurnRotator produceNextRotator(Hand winner) {
        return new EnumTurnRotator(new EnumRotator<Hand>(Hand.values(), winner, handEnumRotator.getValuesToSkip()));
    }
}