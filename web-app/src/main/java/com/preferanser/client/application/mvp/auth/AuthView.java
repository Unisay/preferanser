
package com.preferanser.client.application.mvp.auth;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

import static com.preferanser.client.application.mvp.MD5Util.md5Hex;

public class AuthView extends ViewWithUiHandlers<AuthUiHandlers> implements AuthPresenter.MyView {

    private static final int LOGIN_INDEX = 0;
    private static final int LOGOUT_INDEX = 1;

    public interface Binder extends UiBinder<Widget, AuthView> {}

    @UiField Label authLabel;
    @UiField Hyperlink logout;
    @UiField Hyperlink login;
    @UiField DeckPanel deck;
    @UiField Image avatar;

    @Inject AuthView(Binder binder) {
        initWidget(binder.createAndBindUi(this));
        deck.showWidget(LOGIN_INDEX);
    }

    @Override
    public void displayAuthInfo(String email, String nickname) {
        avatar.setUrl("http://www.gravatar.com/avatar/" + md5Hex(email) + "?s=40&d=blank");
        authLabel.setText(nickname);
        deck.showWidget(LOGOUT_INDEX);
    }

    @UiHandler("login") void onLoginClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().login();
    }

    @UiHandler("logout") void onLogoutClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().logout();
        deck.showWidget(LOGIN_INDEX);
    }

}
