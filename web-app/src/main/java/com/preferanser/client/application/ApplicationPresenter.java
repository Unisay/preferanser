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

package com.preferanser.client.application;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.preferanser.client.application.mvp.auth.AuthPresenter;

public class ApplicationPresenter extends Presenter<ApplicationPresenter.TheView, ApplicationPresenter.TheProxy> {

    public interface TheView extends View {}

    private AuthPresenter authPresenter;

    @ContentSlot
    public static final Type<RevealContentHandler<?>> MAIN_SLOT = new Type<RevealContentHandler<?>>();
    public static final Object AUTH_SLOT = new Object();

    @ProxyStandard
    public interface TheProxy extends Proxy<ApplicationPresenter> {}

    @Inject
    public ApplicationPresenter(EventBus eventBus, TheView view, TheProxy proxy, AuthPresenter authPresenter) {
        super(eventBus, view, proxy, RevealType.Root);
        this.authPresenter = authPresenter;
    }

    @Override protected void onBind() {
        super.onBind();
        setInSlot(AUTH_SLOT, authPresenter);
    }

}
