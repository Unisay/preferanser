
package com.preferanser.client.application.mvp.auth;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.preferanser.shared.dto.CurrentUserDto;

public class AuthPresenter extends PresenterWidget<AuthPresenter.MyView> implements AuthUiHandlers {

    private final CurrentUserDto currentUserDto;

    public interface MyView extends View, HasUiHandlers<AuthUiHandlers> {
        void displayNickname(String nickname);
    }

    @Inject public AuthPresenter(
        EventBus eventBus,
        MyView view,
        CurrentUserDto currentUserDto
    ) {
        super(eventBus, view);
        this.currentUserDto = currentUserDto;
        getView().setUiHandlers(this);
        if (currentUserDto.isLoggedIn)
            getView().displayNickname(currentUserDto.nickname);
    }

    @Override public void login() {
        redirectWithReturn(currentUserDto.loginUrl);
    }

    @Override public void logout() {
        redirectWithReturn(currentUserDto.logoutUrl);
    }

    private void redirectWithReturn(String url) {
        String returnUrl = URL.encode(Window.Location.getHref());
        String redirectUrl = url.replace("%2F", returnUrl);
        Window.Location.assign(redirectUrl);
    }

}