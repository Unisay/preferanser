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

package com.preferanser;

import com.preferanser.domain.Card;
import com.preferanser.requirements.Application;
import com.preferanser.steps.EndUserSteps;
import net.thucydides.core.annotations.Issue;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.pages.Pages;
import org.junit.Test;

/**
 * TODO-ylazarev: write class javadoc
 */
@Story(Application.Cards.CardsPresence.class)
public class CardsPresenceTest extends ThucydidesTest {

    @ManagedPages(defaultUrl = "http://127.0.0.1:8888/Preferanser.html?gwt.codesvr=127.0.0.1:9997")
    public Pages pages;

    @Steps
    public EndUserSteps endUser;

    @Issue("#WIKI-1")
    @Test
    public void all_cards_should_be_present_on_north() {
        endUser.is_the_table_page();
        for (Card card : Card.values()) {
            endUser.can_see(card);
        }
    }

}
