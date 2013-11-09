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

import com.preferanser.domain.Card;
import com.preferanser.domain.TableLocation;
import com.preferanser.webtest.pages.TablePage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

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
        TablePage page = getPages().get(TablePage.class);
        for (Card card : cards) {
            assertTrue(page.cardIsVisibleAtLocation(card, location));
        }
        return this;
    }

    @Step
    public void canSeeNoCardsAt(TableLocation... locations) {
        TablePage page = getPages().get(TablePage.class);
        for (TableLocation location : locations) {
            assertTrue(page.locationHasNoCards(location));
        }
    }
}