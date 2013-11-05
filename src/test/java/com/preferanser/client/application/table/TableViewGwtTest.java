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

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.theme.greencloth.client.com.preferanser.client.application.PreferanserResources;

/**
 * Gwt-Test for the table editing functionality
 */
public class TableViewGwtTest extends GWTTestCase {

    private TableView tableView;

    @Override
    public String getModuleName() {
        return "com.preferanser.Preferanser";
    }

    @Override
    public void gwtSetUp() throws Exception {
        TableView.Binder binder = GWT.create(TableView.Binder.class);
        PreferanserResources resources = GWT.create(PreferanserResources.class);
        PreferanserConstants constants = GWT.create(PreferanserConstants.class);
        tableView = new TableView(binder, resources, constants);
    }

    public void testView() throws Exception {
        assertNotNull(tableView);
    }
}