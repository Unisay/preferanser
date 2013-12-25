package com.preferanser.domain.exception;

import com.preferanser.domain.Suit;

public class IllegalSuitException extends GameException {

    private Suit expectedSuit;
    private Suit actualSuit;

    @SuppressWarnings("unused") // required for serialization
    public IllegalSuitException() {
    }

    public IllegalSuitException(Suit expectedSuit, Suit actualSuit) {
        this.expectedSuit = expectedSuit;
        this.actualSuit = actualSuit;
    }

    @Override public String getMessage() {
        return "Expected " + expectedSuit + " suit, got " + actualSuit + " instead.";
    }

    public Suit getExpectedSuit() {
        return expectedSuit;
    }

    public Suit getActualSuit() {
        return actualSuit;
    }
}
