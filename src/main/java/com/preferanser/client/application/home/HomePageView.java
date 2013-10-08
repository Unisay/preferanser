/**
 * Copyright 2012 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
