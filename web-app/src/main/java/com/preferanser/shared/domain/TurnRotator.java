package com.preferanser.shared.domain;

/**
 * Rotates turns
 */
public interface TurnRotator {
    Hand current();
    void next();
    void prev();
    TurnRotator produceNextRotator(Hand winner);
}
