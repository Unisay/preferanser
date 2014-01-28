package com.preferanser.server.exception;

public class NotAuthorizedUserException extends ServerException {

    public NotAuthorizedUserException() {
        this("Not authorized");
    }

    public NotAuthorizedUserException(String message) {
        super(message);
    }

}
