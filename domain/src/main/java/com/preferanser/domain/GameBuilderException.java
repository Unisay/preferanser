package com.preferanser.domain;

import java.util.Collection;

public class GameBuilderException extends Exception {

    private Collection<Game.Builder.BuilderError> builderErrors;

    public GameBuilderException() {
    }

    public GameBuilderException(Collection<Game.Builder.BuilderError> builderErrors) {
        this.builderErrors = builderErrors;
    }

    public Collection<Game.Builder.BuilderError> getBuilderErrors() {
        return builderErrors;
    }
}
