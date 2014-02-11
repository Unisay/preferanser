package com.preferanser.client.gwtp;

import com.google.common.base.Optional;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

import java.util.logging.Logger;

public class PlaceRequestHelper {

    private static final Logger log = Logger.getLogger("PlaceRequestHelper");

    private PlaceRequest request;

    public PlaceRequestHelper(PlaceRequest request) {
        this.request = request;
    }

    public Optional<Long> parseLongParameter(String parameter) {
        String value = request.getParameter(parameter, "");
        if (!value.isEmpty()) {
            try {
                return Optional.of(Long.parseLong(value));
            } catch (NumberFormatException e) {
                log.warning("Failed to parse parameter '" + parameter + "' as Long");
            }
        }
        return Optional.absent();
    }

    public Optional<Double> parseDoubleParameter(String parameter) {
        String value = request.getParameter(parameter, "");
        if (!value.isEmpty()) {
            try {
                return Optional.of(Double.parseDouble(value));
            } catch (NumberFormatException e) {
                log.warning("Failed to parse parameter '" + parameter + "' as Double");
            }
        }
        return Optional.absent();
    }

    public Optional<String> getStringParameter(String parameter) {
        return Optional.fromNullable(request.getParameter(parameter, null));
    }
}
