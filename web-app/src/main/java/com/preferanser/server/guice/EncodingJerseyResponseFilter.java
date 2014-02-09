package com.preferanser.server.guice;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class EncodingJerseyResponseFilter implements ContainerResponseFilter {

    @Override public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        MultivaluedMap<String, Object> headers = response.getHttpHeaders();
        Object first = headers.getFirst(HttpHeaders.CONTENT_TYPE);
        String contentType = first == null
            ? String.format("%s; charset=utf-8", MediaType.APPLICATION_JSON)
            : String.format("%s; charset=utf-8", first.toString());
        headers.putSingle(HttpHeaders.CONTENT_TYPE, contentType);
        return response;
    }

}
