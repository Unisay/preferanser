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

package com.preferanser.client.application.widgets;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Does not represent an HTML element, only holds child widgets.
 */
public class Widgets implements HasWidgets {

    private ArrayList<Widget> widgets = Lists.newArrayList();

    @Override public void add(Widget w) {
        widgets.add(w);
    }

    @Override public void clear() {
        widgets.clear();
    }

    @Override public Iterator<Widget> iterator() {
        return widgets.iterator();
    }

    @Override public boolean remove(Widget w) {
        return widgets.remove(w);
    }
}
