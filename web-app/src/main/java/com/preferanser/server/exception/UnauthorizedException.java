package com.preferanser.server.exception;

import javax.ws.rs.core.Response;

public class UnauthorizedException extends WebApplicationExceptionWithMessage {

    public UnauthorizedException() {
        super(Response.Status.UNAUTHORIZED);
    }

    public UnauthorizedException(String message) {
        super(Response.Status.UNAUTHORIZED, message);
    }

}
