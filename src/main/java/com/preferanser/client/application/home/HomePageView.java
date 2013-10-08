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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.preferanser.client.application.home.ui.MyEntityEditor;
import com.preferanser.client.request.proxy.UserProxy;

import java.util.List;

public class HomePageView extends ViewWithUiHandlers<HomeUiHandlers> implements HomePagePresenter.MyView {
    public interface Binder extends UiBinder<Widget, HomePageView> {
    }

    @UiField(provided = true)
    MyEntityEditor myEntityEditor;
    @UiField(provided = true)
    CellTable<UserProxy> myTable;

    private final ListDataProvider<UserProxy> dataProvider;

    @Inject
    public HomePageView(final Binder uiBinder, final MyEntityEditor myEntityEditor,
                        final ListDataProvider<UserProxy> dataProvider) {
        this.myEntityEditor = myEntityEditor;
        this.dataProvider = dataProvider;
        this.myTable = new CellTable<UserProxy>();

        initWidget(uiBinder.createAndBindUi(this));

        initCellTable();
        dataProvider.addDataDisplay(myTable);
    }

    @Override
    public void editUser(UserProxy myEntity) {
        myEntityEditor.edit(myEntity);
    }

    @Override
    public void setData(List<UserProxy> data) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(data);
        dataProvider.refresh();
    }

    @UiHandler("submit")
    void onSubmitClicked(ClickEvent event) {
        getUiHandlers().saveEntity(myEntityEditor.get());
    }

    private void initCellTable() {
        TextColumn<UserProxy> firstNameColumn = new TextColumn<UserProxy>() {
            @Override
            public String getValue(UserProxy object) {
                return object.getFirstName();
            }
        };
        myTable.addColumn(firstNameColumn, "First name");

        TextColumn<UserProxy> lastNameColumn = new TextColumn<UserProxy>() {
            @Override
            public String getValue(UserProxy object) {
                return object.getLastName();
            }
        };
        myTable.addColumn(lastNameColumn, "Last name");
    }
}
