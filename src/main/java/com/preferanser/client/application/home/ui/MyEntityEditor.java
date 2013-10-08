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

package com.preferanser.client.application.home.ui;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.preferanser.client.request.proxy.UserProxy;

public class MyEntityEditor extends Composite implements Editor<UserProxy> {
    public interface Binder extends UiBinder<Widget, MyEntityEditor> {
    }

    public interface Driver extends SimpleBeanEditorDriver<UserProxy, MyEntityEditor> {
    }

    @UiField
    TextBox firstName;
    @UiField
    TextBox lastName;

    private final Driver driver;

    @Inject
    public MyEntityEditor(Binder uiBinder, Driver driver) {
        this.driver = driver;

        initWidget(uiBinder.createAndBindUi(this));
        driver.initialize(this);
    }

    public void edit(UserProxy myEntity) {
        firstName.setFocus(true);
        driver.edit(myEntity);
    }

    public UserProxy get() {
        UserProxy myEntity = driver.flush();
        if (driver.hasErrors()) {
            return null;
        } else {
            return myEntity;
        }
    }
}