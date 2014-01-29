
package com.preferanser.client.application.mvp.auth;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class AuthModule extends AbstractPresenterModule {

    @Override
    protected void configure() {
        bindPresenterWidget(AuthPresenter.class, AuthPresenter.MyView.class, AuthView.class);
    }
}
