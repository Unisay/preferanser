
package com.preferanser.client.application.mvp.auth;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.preferanser.shared.domain.User;

import static com.google.common.base.Preconditions.checkNotNull;

public class AuthPresenter extends PresenterWidget<AuthPresenter.MyView> implements AuthUiHandlers {

    private final User user;

    public interface MyView extends View, HasUiHandlers<AuthUiHandlers> {
        void displayAuthInfo(String email, String nickname);
    }

    @Inject public AuthPresenter(EventBus eventBus, MyView view, User user) {
        super(eventBus, view);
        this.user = user;
        getView().setUiHandlers(this);
        if (user.getLoggedIn())
            getView().displayAuthInfo(user.getEmail(), user.getNickname());
    }

    @Override public void login() {
        redirectWithReturn(user.getLoginUrl());
    }

    @Override public void logout() {
        redirectWithReturn(user.getLogoutUrl());
    }

    private void redirectWithReturn(String url) {
        checkNotNull(url, "Redirect URL is null");
        String returnUrl = URL.encode(Window.Location.getHref());
        String redirectUrl = url.replace("%2F", returnUrl);
        Window.Location.assign(redirectUrl);
    }

}