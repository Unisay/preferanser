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

package com.preferanser.client.application.mvp.editor.dialog.open;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.preferanser.shared.domain.entity.Deal;

import java.util.Collection;
import java.util.List;

public class OpenDialogPresenter extends PresenterWidget<OpenDialogPresenter.TheView> implements OpenDialogUiHandlers {

    private List<Deal> deals;

    public interface TheView extends PopupView, HasUiHandlers<OpenDialogUiHandlers> {
        void displayAvailableDeals(Collection<Deal> deals);
    }

    @Inject
    public OpenDialogPresenter(EventBus eventBus, TheView view) {
        super(eventBus, view);
        getView().setUiHandlers(this);
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        getView().displayAvailableDeals(deals);
    }

    public void setDeals(List<Deal> deals) {
        this.deals = deals;
    }

}
