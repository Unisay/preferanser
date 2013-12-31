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

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;
import com.preferanser.shared.domain.entity.Deal;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class OpenDialogView extends PopupViewWithUiHandlers<OpenDialogUiHandlers> implements OpenDialogPresenter.TheView {

    @UiField(provided = true)
    CellList<String> cellList;

    interface Binder extends UiBinder<PopupPanel, OpenDialogView> {
    }

    @Inject
    protected OpenDialogView(Binder uiBinder, EventBus eventBus) {
        super(eventBus);
        cellList = new CellList<String>(new TextCell());
        cellList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void displayAvailableDeals(Collection<Deal> deals) {
        // TODO: handle empty deals
        cellList.setRowCount(deals.size());
        List<String> labels = newArrayList();
        for (Deal deal : deals) {
            labels.add(deal.getCreated() + " " + deal.getName());
        }
        cellList.setRowData(0, labels);
    }

}
