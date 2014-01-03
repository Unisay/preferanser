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
@SuppressWarnings("ClassWithTooManyMethods")
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
    @DefaultStringValue("To sluff") String toSluff();
    @DefaultStringValue("Deal") String deal();
    @DefaultStringValue("Save") String save();
    @DefaultStringValue("Saving") String saving();
    @DefaultStringValue("Saved") String saved();
    @DefaultStringValue("Open") String open();
    @DefaultStringValue("Delete") String delete();
    @DefaultStringValue("Loading") String loading();
    @DefaultStringValue("Loaded") String loaded();

    @DefaultStringValue("Please choose name for the deal") String saveDescription();
    @DefaultStringValue("Choose contract") String chooseContract();

    String SPADE_7();
    String CLUB_7();
    String DIAMOND_7();
    String HEART_7();
    String SPADE_8();
    String CLUB_8();
    String DIAMOND_8();
    String HEART_8();
    String SPADE_9();
    String CLUB_9();
    String DIAMOND_9();
    String HEART_9();
    String SPADE_10();
    String CLUB_10();
    String DIAMOND_10();
    String HEART_10();
    String SPADE_JACK();
    String CLUB_JACK();
    String DIAMOND_JACK();
    String HEART_JACK();
    String SPADE_QUEEN();
    String CLUB_QUEEN();
    String DIAMOND_QUEEN();
    String HEART_QUEEN();
    String SPADE_KING();
    String CLUB_KING();
    String DIAMOND_KING();
    String HEART_KING();
    String SPADE_ACE();
    String CLUB_ACE();
    String DIAMOND_ACE();
    String HEART_ACE();
}
