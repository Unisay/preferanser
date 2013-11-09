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

package com.preferanser.webtest.pages;

import com.google.common.base.Optional;
import com.preferanser.domain.Card;
import com.preferanser.domain.TableLocation;
import net.thucydides.core.annotations.At;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.base.Optional.absent;

/**
 * Table page
 */
@At("http://127.0.0.1:8888/Preferanser.html")
@DefaultUrl("http://127.0.0.1:8888/Preferanser.html?gwt.codesvr=127.0.0.1:9997")
public class TablePage extends GwtPage {

    private static final Logger log = LoggerFactory.getLogger(TablePage.class);

    public TablePage(WebDriver driver) {
        super(driver);
    }

    public Optional<WebElementFacade> getCardAtTableLocationElement(Card card, TableLocation location) {
        Optional<WebElementFacade> maybeTableLocation = getTableLocationElement(location);
        if (maybeTableLocation.isPresent()) {
            try {
                WebElementFacade cardElementFacade = maybeTableLocation.get().findBy(getGwtId(card.name()));
                return Optional.of(cardElementFacade);
            } catch (NoSuchElementException e) {
                log.warn("Element not found", e);
                return absent();
            }
        }
        return absent();
    }

    public List<WebElement> getTableLocationCards(TableLocation location) {
        Optional<WebElementFacade> maybeTableLocation = getTableLocationElement(location);
        if (!maybeTableLocation.isPresent())
            throw new IllegalStateException(String.format("TableLocation.%s web element is not present on page", location));
        return maybeTableLocation.get().getWrappedElement().findElements(By.className("card"));
    }

    public Optional<WebElementFacade> getTableLocationElement(TableLocation location) {
        WebElementFacade locationElementFacade = gwtElementById(location.name());
        if (locationElementFacade.isCurrentlyVisible()) {
            return Optional.fromNullable(locationElementFacade);
        }
        return absent();
    }

    public WebElementFacade getEditButton() {
        return gwtElementById("edit");
    }

    public WebElementFacade getResetButton() {
        return gwtElementById("reset");
    }
}
