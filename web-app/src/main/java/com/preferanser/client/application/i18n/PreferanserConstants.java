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

package com.preferanser.client.application.i18n;

import com.google.gwt.i18n.client.ConstantsWithLookup;

/**
 * Preferanser i18n constants
 * <p/>
 * See <a href="http://www.gwtproject.org/doc/latest/DevGuideI18n.html">GWT i18n doc</a>
 */
public interface PreferanserConstants extends ConstantsWithLookup {
    @DefaultStringValue("Preferanser") String preferanser();
    @DefaultStringValue("Pass") String pass();
    @DefaultStringValue("Whist") String whist();
    @DefaultStringValue("Miser") String miser();
    @DefaultStringValue("No Trump") String noTrump();
    @DefaultStringValue("North") String NORTH();
    @DefaultStringValue("East") String EAST();
    @DefaultStringValue("South") String SOUTH();
    @DefaultStringValue("North") String WEST();
    @DefaultStringValue("♠") String SPADE_char();
    @DefaultStringValue("♣") String CLUB_char();
    @DefaultStringValue("♦") String DIAMOND_char();
    @DefaultStringValue("♥") String HEART_char();
    @DefaultStringValue("Play") String play();
    @DefaultStringValue("Enter") String enter();

    @DefaultStringValue("Edit") String edit();
    @DefaultStringValue("North") String north();
    @DefaultStringValue("East") String east();
    @DefaultStringValue("South") String south();
    @DefaultStringValue("West") String west();
    @DefaultStringValue("To sluff") String toSluff();
    @DefaultStringValue("Deal") String deal();
    @DefaultStringValue("Save") String save();
    @DefaultStringValue("Saving") String saving();
    @DefaultStringValue("Saved") String saved();
    @DefaultStringValue("Open") String open();
    @DefaultStringValue("Loading") String loading();
    @DefaultStringValue("Loaded") String loaded();

    @DefaultStringValue("Please choose name for the deal") String saveDescription();
    @DefaultStringValue("Choose contract") String chooseContract();
    @DefaultStringValue("Wrong number of contracts") String WRONG_NUMBER_OF_CONTRACTS();
    @DefaultStringValue("There are conflicting contracts") String HAS_CONFLICTING_CONTRACTS();
    @DefaultStringValue("Fist turn not specified") String FIRST_TURN_NOT_SPECIFIED();
    @DefaultStringValue("First turn specified incorrectly") String WRONG_FIRST_TURN();
    @DefaultStringValue("Cards dealt incorrectly") String WRONG_CARDINAL_CARDS();

    @DefaultStringValue("Number of players not specified") String NUM_PLAYERS_NOT_SPECIFIED();

}
