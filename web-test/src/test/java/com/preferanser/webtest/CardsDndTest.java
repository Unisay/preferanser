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

package com.preferanser.webtest;

import com.preferanser.domain.Card;
import com.preferanser.domain.TableLocation;
import com.preferanser.webtest.requirements.Application;
import net.thucydides.core.annotations.Story;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import static com.preferanser.domain.Card.*;
import static com.preferanser.domain.TableLocation.*;

@Story(Application.Table.Cards.DragAndDrop.class)
public class CardsDndTest extends TableTest {

    @Test
    public void user_can_drag_card_to_east() {
        user_can_drag_card_to_location(SPADE_ACE, NORTH, EAST);
    }

    @Test
    public void user_can_drag_card_to_south() {
        user_can_drag_card_to_location(CLUB_ACE, NORTH, SOUTH);
    }

    @Test
    public void user_can_drag_card_to_west() {
        user_can_drag_card_to_location(DIAMOND_ACE, NORTH, WEST);
    }

    @Test
    public void user_can_drag_card_to_center() {
        user_can_drag_card_to_location(HEART_ACE, NORTH, CENTER);
    }

    private void user_can_drag_card_to_location(Card card, TableLocation fromLocation, TableLocation toLocation) {
        endUser.onTheTablePage()
                .editsNewDeal()
                .dragsCardToOtherLocation(card, fromLocation, toLocation)
                .canSeeCardsAt(toLocation, card)
                .canSeeNoCardsAt(ArrayUtils.removeElements(TableLocation.values(), toLocation, fromLocation));
    }


}