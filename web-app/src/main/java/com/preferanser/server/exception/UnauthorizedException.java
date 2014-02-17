package com.preferanser.server.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class UnauthorizedException extends WebApplicationException {

    public UnauthorizedException(String message) {
        super(Response.status(Response.Status.UNAUTHORIZED).type(MediaType.TEXT_PLAIN_TYPE).entity(message).build());
    }

}
