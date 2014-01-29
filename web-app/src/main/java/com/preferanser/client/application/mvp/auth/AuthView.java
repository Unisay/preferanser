
package com.preferanser.client.application.mvp.auth;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.preferanser.client.application.i18n.PreferanserMessages;

public class AuthView extends ViewWithUiHandlers<AuthUiHandlers> implements AuthPresenter.MyView {

    private static final int LOGIN_INDEX = 0;
    private static final int LOGOUT_INDEX = 1;

    private final PreferanserMessages preferanserMessages;

    public interface Binder extends UiBinder<Widget, AuthView> {}

    @UiField InlineHTML authLabel;
    @UiField Hyperlink logout;
    @UiField Hyperlink login;
    @UiField DeckPanel deck;

    @Inject AuthView(Binder binder, PreferanserMessages preferanserMessages) {
        this.preferanserMessages = preferanserMessages;
        initWidget(binder.createAndBindUi(this));
        deck.showWidget(LOGIN_INDEX);
    }

    @Override
    public void displayNickname(String nickname) {
        authLabel.setHTML(preferanserMessages.loggedInAs(nickname));
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
