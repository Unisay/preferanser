package com.preferanser.shared.domain.exception;

/**
 * Can't retrieve cards from empty widow
 */
public class EmptyWidowException extends GameException {

    public EmptyWidowException() {
        super("Widow is empty");
    }

}
