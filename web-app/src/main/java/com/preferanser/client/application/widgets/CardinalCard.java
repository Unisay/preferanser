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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.preferanser.shared.domain.Cardinal;

public class CardinalCard implements IsWidget {

    private Cardinal cardinal;
    private CardWidget cardWidget;

    public CardinalCard(Cardinal cardinal, CardWidget cardWidget) {
        this.cardinal = cardinal;
        this.cardWidget = cardWidget;
    }

    public Cardinal getCardinal() {
        return cardinal;
    }

    public CardWidget getCardWidget() {
        return cardWidget;
    }

    @Override
    public Widget asWidget() {
        return cardWidget;
    }

}
