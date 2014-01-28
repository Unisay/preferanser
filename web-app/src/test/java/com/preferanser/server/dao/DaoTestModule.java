package com.preferanser.server.dao;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.preferanser.server.guice.HibernateValidatorProvider;

import javax.validation.Validator;

public class DaoTestModule extends AbstractModule {

    @Override protected void configure() {
        bind(Validator.class).toProvider(HibernateValidatorProvider.class).in(Singleton.class);
    }

}
