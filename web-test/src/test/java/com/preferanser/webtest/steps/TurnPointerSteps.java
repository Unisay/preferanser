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

import com.preferanser.shared.domain.Hand;
import com.preferanser.webtest.pages.TablePage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.steps.ScenarioSteps;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TurnPointerSteps extends ScenarioSteps {

    private EndUserSteps endUserSteps;

    public TurnPointerSteps(EndUserSteps endUserSteps) {
        super(endUserSteps.getPages());
        this.endUserSteps = endUserSteps;
    }

    private TablePage getTablePage() {
        return getPages().get(TablePage.class);
    }

    @Step
    public TurnPointerSteps activatesTurnPointer(Hand hand) {
        getTablePage().getHandTurnPointer(hand).click();
        return this;
    }

    @Step
    public TurnPointerSteps canSeeOnlyTurnPointerActive(Hand hand) {
        TablePage page = getTablePage();
        Matcher<String> isActive = containsString("turnPointerActive");
        for (Hand currentHand : Hand.PLAYING_HANDS) {
            Matcher<String> matcher = currentHand == hand ? isActive : not(isActive);
            assertThat(page.getHandTurnPointer(currentHand).getAttribute("class"), matcher);
        }
        return this;
    }

    @Step
    public TurnPointerSteps canSeeOnlyTurnPointer(Hand hand) {
        TablePage page = getTablePage();
        Matcher<String> isActive = containsString("turnPointerActive");
        Matcher<String> notDisplayed = containsString("not-displayed");
        for (Hand currentHand : Hand.PLAYING_HANDS) {
            Matcher<String> matcher = currentHand == hand ? isActive : notDisplayed;
            assertThat(page.getHandTurnPointer(currentHand).getAttribute("class"), matcher);
        }
        return this;
    }

    public TurnPointerSteps canSeeNoActiveTurnPointer() {
        return canSeeOnlyTurnPointerActive(null);
    }

    public EndUserSteps withTable() {
        return endUserSteps;
    }
}