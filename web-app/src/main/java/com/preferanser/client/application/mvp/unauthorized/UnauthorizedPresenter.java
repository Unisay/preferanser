/*
 * Preferanser is a program to simulate and calculate Russian Preferans Card game deals.
 *
 *     Copyright (C) 2013  Yuriy Lazarev <Yuriy.Lazarev@gmail.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see [http://www.gnu.org/licenses/].
 */

package com.preferanser.client.application.mvp.unauthorized;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.preferanser.client.application.ApplicationPresenter;
import com.preferanser.client.gwtp.NameTokens;
import com.preferanser.shared.domain.User;

/**
 * Presenter for the unauthorized page
 */
public class UnauthorizedPresenter extends Presenter<UnauthorizedPresenter.TheView, UnauthorizedPresenter.TheProxy> {

    @NoGatekeeper
    @ProxyStandard
    @NameToken(NameTokens.UNAUTHORIZED)
    public interface TheProxy extends ProxyPlace<UnauthorizedPresenter> {}

    public interface TheView extends View {}

    private final User user;

    @Inject
    public UnauthorizedPresenter(EventBus eventBus, TheView view, TheProxy proxy, User user) {
        super(eventBus, view, proxy, ApplicationPresenter.MAIN_SLOT);
        this.user = user;
    }

    @Override protected void onReveal() {
        String returnUrl = URL.encode(Window.Location.getHref());
        String loginUrl = user.getLoginUrl().replace("%2F", returnUrl);
        Window.Location.assign(loginUrl);
    }
}
