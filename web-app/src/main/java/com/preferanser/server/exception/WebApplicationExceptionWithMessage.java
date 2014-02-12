package com.preferanser.server.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class WebApplicationExceptionWithMessage extends WebApplicationException {

    public WebApplicationExceptionWithMessage(Response.Status status) {
        super(status);
    }

    public WebApplicationExceptionWithMessage(Response.Status status, String message) {
        super(Response.status(status).type(MediaType.TEXT_PLAIN_TYPE).entity(message).build());
    }

}
