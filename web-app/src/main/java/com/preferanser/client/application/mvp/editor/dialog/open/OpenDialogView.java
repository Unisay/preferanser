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

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.laf.client.PreferanserResources;
import com.preferanser.shared.domain.entity.Deal;

import java.util.Date;
import java.util.List;


public class OpenDialogView extends PopupViewWithUiHandlers<OpenDialogUiHandlers> implements OpenDialogPresenter.TheView {

    @UiField(provided = true)
    DataGrid<Deal> dealDataGrid;

    private final ListDataProvider<Deal> dealDataProvider;

    interface Binder extends UiBinder<PopupPanel, OpenDialogView> {
    }

    @Inject
    protected OpenDialogView(
        Binder uiBinder,
        EventBus eventBus,
        PreferanserResources resources,
        PreferanserConstants constants
    ) {
        super(eventBus);
        dealDataGrid = new DataGrid<Deal>(10, resources.dataGrid(), new ProvidesDealKey());
        dealDataGrid.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);
        dealDataGrid.addColumn(new DealNameColumn());
        dealDataGrid.addColumn(new DealDateColumn());
        dealDataGrid.addColumn(new OpenActionColumn(constants.open()));
        dealDataGrid.addColumn(new RemoveActionColumn(constants.delete()));

        dealDataGrid.setColumnWidth(1, 110.0, Style.Unit.PX);
        dealDataGrid.setColumnWidth(2, 95.0, Style.Unit.PX);
        dealDataGrid.setColumnWidth(3, 95.0, Style.Unit.PX);
        dealDataProvider = new ListDataProvider<Deal>();
        dealDataProvider.addDataDisplay(dealDataGrid);

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void displayAvailableDeals(List<Deal> deals) {
        // TODO: handle empty deals
        dealDataProvider.setList(deals);
    }

    private static class ProvidesDealKey implements ProvidesKey<Deal> {
        @Override
        public Object getKey(Deal item) {
            return item.getId();
        }
    }

    private static class DealNameColumn extends TextColumn<Deal> {
        @Override
        public String getValue(Deal deal) {
            return deal.getName();
        }
    }

    private static class DealDateColumn extends Column<Deal, Date> {
        public DealDateColumn() {
            super(new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT)));
        }

        @Override
        public Date getValue(Deal object) {
            return object.getCreated();
        }
    }

    private class OpenActionColumn extends Column<Deal, Deal> {
        private OpenActionColumn(String text) {
            super(new ActionCell<Deal>(text, new ActionCell.Delegate<Deal>() {
                @Override
                public void execute(Deal deal) {
                    getUiHandlers().onDealOpenClicked(deal);
                }
            }));
        }

        @Override
        public Deal getValue(Deal deal) {
            return deal;
        }
    }

    private class RemoveActionColumn extends Column<Deal, Deal> {
        private RemoveActionColumn(String text) {
            super(new ActionCell<Deal>(text, new ActionCell.Delegate<Deal>() {
                @Override
                public void execute(Deal deal) {
                    getUiHandlers().deleteDeal(deal);
                }
            }));
        }

        @Override
        public Deal getValue(Deal deal) {
            return deal;
        }
    }
}
