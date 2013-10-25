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

package com.preferanser.client.application.table;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.preferanser.shared.Cardinal;

public class ContractDialogPresenterWidget extends PresenterWidget<ContractDialogPresenterWidget.MyView> {

    private Cardinal cardinal;

    public interface MyView extends PopupView {
        void setCardinal(Cardinal cardinal);
    }

    @Inject
    public ContractDialogPresenterWidget(EventBus eventBus, MyView view) {
        super(eventBus, view);
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        getView().setCardinal(this.cardinal);
    }

    public void setCardinal(Cardinal cardinal) {
        this.cardinal = cardinal;
    }

    public Cardinal getCardinal() {
        return cardinal;
    }
}
