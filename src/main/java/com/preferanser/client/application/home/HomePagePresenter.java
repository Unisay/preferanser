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

package com.preferanser.client.application.home;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.preferanser.client.application.ApplicationPresenter;
import com.preferanser.client.place.NameTokens;
import com.preferanser.client.request.MyRequestFactory;
import com.preferanser.client.request.MyServiceRequest;
import com.preferanser.client.request.proxy.UserProxy;

import java.util.List;

public class HomePagePresenter extends Presenter<HomePagePresenter.MyView, HomePagePresenter.MyProxy> implements
        HomeUiHandlers {
    public interface MyView extends View, HasUiHandlers<HomeUiHandlers> {
        void editUser(UserProxy myEntity);

        void setData(List<UserProxy> data);
    }

    @ProxyStandard
    @NameToken(NameTokens.home)
    public interface MyProxy extends ProxyPlace<HomePagePresenter> {
    }

    private final MyRequestFactory requestFactory;

    private MyServiceRequest currentContext;
    private String searchToken;

    @Inject
    public HomePagePresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
                             final MyRequestFactory requestFactory) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);

        this.requestFactory = requestFactory;

        getView().setUiHandlers(this);
    }

    @Override
    public void saveEntity(UserProxy myEntity) {
        currentContext.create(myEntity).fire(new Receiver<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadEntities();
                initializeContext();
            }
        });
    }

    @Override
    protected void onReveal() {
        searchToken = "";
        initializeContext();
        loadEntities();
    }

    private void initializeContext() {
        currentContext = requestFactory.myService();
        UserProxy newEntity = currentContext.create(UserProxy.class);
        getView().editUser(newEntity);
    }

    private void loadEntities() {
        requestFactory.myService().loadAll(searchToken).fire(new Receiver<List<UserProxy>>() {
            @Override
            public void onSuccess(List<UserProxy> data) {
                getView().setData(data);
            }
        });
    }
}
