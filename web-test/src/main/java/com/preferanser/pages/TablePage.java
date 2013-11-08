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

package com.preferanser.pages;

import com.preferanser.domain.Card;
import com.preferanser.domain.TableLocation;
import net.thucydides.core.annotations.At;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Table page
 */
@At("http://127.0.0.1:8888/Preferanser.html")
@DefaultUrl("http://127.0.0.1:8888/Preferanser.html?gwt.codesvr=127.0.0.1:9997")
public class TablePage extends GwtPage {

    public TablePage(WebDriver driver) {
        super(driver);
    }

    public boolean cardIsVisibleAtLocation(Card card, TableLocation location) {
        WebElementFacade elementFacade = gwtElementById(location.name());
        return elementFacade.isCurrentlyVisible() && elementFacade.findBy(getGwtId(card.name())).isCurrentlyVisible();
    }

    public boolean locationHasNoCards(TableLocation location) {
        WebElementFacade locationElement = gwtElementById(location.name());
        return locationElement.getWrappedElement().findElements(By.className("card")).isEmpty();
    }
}
