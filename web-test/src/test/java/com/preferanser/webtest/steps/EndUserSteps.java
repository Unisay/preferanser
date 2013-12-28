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

package com.preferanser.webtest.steps;

import com.google.common.base.Optional;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.Cardinal;
import com.preferanser.shared.domain.Contract;
import com.preferanser.shared.domain.TableLocation;
import com.preferanser.webtest.pages.TablePage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.steps.ScenarioSteps;
import org.hamcrest.collection.IsEmptyCollection;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EndUserSteps extends ScenarioSteps {

    public EndUserSteps(Pages pages) {
        super(pages);
    }

    @Step
    public EndUserSteps onTheTablePage() {
        getPages().currentPageAt(TablePage.class);
        return this;
    }

    @Step
    public EndUserSteps canSeeCardsAt(TableLocation location, Card... cards) {
        TablePage page = getTablePage();
        for (Card card : cards) {
            Optional<WebElementFacade> maybeCardAtTableLocationElement = page.getCardAtTableLocationElement(card, location);

            assertTrue(format("Can't find card %s at location %s", card, location),
                maybeCardAtTableLocationElement.isPresent());

            assertTrue(format("Card %s is not visible at location %s", card, location),
                maybeCardAtTableLocationElement.get().isCurrentlyVisible());
        }
        return this;
    }

    @Step
    public EndUserSteps canSeeNoCardsAt(TableLocation... locations) {
        TablePage page = getTablePage();
        for (TableLocation location : locations) {
            assertThat(page.getTableLocationCards(location), IsEmptyCollection.empty());
        }
        return this;
    }

    @Step
    public EndUserSteps dragsCardToOtherLocation(Card card, TableLocation fromLocation, TableLocation toLocation) {
        TablePage page = getTablePage();

        Optional<WebElementFacade> maybeCard = page.getCardAtTableLocationElement(card, fromLocation);
        assertTrue(maybeCard.isPresent());

        Optional<WebElementFacade> maybeTableLocation = page.getTableLocationElement(toLocation);
        assertTrue(maybeTableLocation.isPresent());

        Mouse mouse = ((HasInputDevices) getDriver()).getMouse();
        mouse.mouseMove(maybeCard.get().getCoordinates(), 3, 3); // grab the top left corner
        mouse.mouseDown(null); // at the current location
        mouse.mouseMove(maybeTableLocation.get().getCoordinates());
        mouse.mouseUp(maybeTableLocation.get().getCoordinates());
        return this;
    }

    @Step
    public EndUserSteps editsNewDeal() {
        TablePage tablePage = getTablePage();
        tablePage.getEditButton().click();
        tablePage.getResetButton().click();
        return this;
    }

    @Step
    public EndUserSteps resetsEditedDeal() {
        TablePage tablePage = getTablePage();
        tablePage.getResetButton().click();
        return this;
    }

    @Step
    public EndUserSteps specifiesContract(Cardinal cardinal, Contract contract) {
        TablePage tablePage = getTablePage();
        tablePage.getContractLink(cardinal).click();
        tablePage.getContractButton(contract).click();
        return this;
    }

    @Step
    public EndUserSteps switchesToPlayMode() {
        TablePage tablePage = getTablePage();
        tablePage.getPlayButton().click();
        return this;
    }

    @Step
    public EndUserSteps canSeeContract(Cardinal cardinal, String contractName) {
        TablePage tablePage = getTablePage();
        assertThat(tablePage.getContractLabel(cardinal).getTextValue(), containsString(" — " + contractName));
        return this;
    }

    @Step
    public EndUserSteps canSeeNoContract(Cardinal cardinal) {
        TablePage tablePage = getTablePage();
        assertThat(tablePage.getContractLabel(cardinal).getTextValue(), equalTo(""));
        return this;
    }

    @Step
    public TurnPointerSteps withTurnPointer() {
        return new TurnPointerSteps(this);
    }

    private TablePage getTablePage() {
        return getPages().get(TablePage.class);
    }
}