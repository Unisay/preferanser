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

    @DefaultStringValue("SPADE_7") String SPADE_7();

    @DefaultStringValue("CLUB_7") String CLUB_7();

    @DefaultStringValue("DIAMOND_7") String DIAMOND_7();

    @DefaultStringValue("HEART_7") String HEART_7();

    @DefaultStringValue("SPADE_8") String SPADE_8();

    @DefaultStringValue("CLUB_8") String CLUB_8();

    @DefaultStringValue("DIAMOND_8") String DIAMOND_8();

    @DefaultStringValue("HEART_8") String HEART_8();

    @DefaultStringValue("SPADE_9") String SPADE_9();

    @DefaultStringValue("CLUB_9") String CLUB_9();

    @DefaultStringValue("DIAMOND_9") String DIAMOND_9();

    @DefaultStringValue("HEART_9") String HEART_9();

    @DefaultStringValue("SPADE_10") String SPADE_10();

    @DefaultStringValue("CLUB_10") String CLUB_10();

    @DefaultStringValue("DIAMOND_10") String DIAMOND_10();

    @DefaultStringValue("HEART_10") String HEART_10();

    @DefaultStringValue("SPADE_JACK") String SPADE_JACK();

    @DefaultStringValue("CLUB_JACK") String CLUB_JACK();

    @DefaultStringValue("DIAMOND_JACK") String DIAMOND_JACK();

    @DefaultStringValue("HEART_JACK") String HEART_JACK();

    @DefaultStringValue("SPADE_QUEEN") String SPADE_QUEEN();

    @DefaultStringValue("CLUB_QUEEN") String CLUB_QUEEN();

    @DefaultStringValue("DIAMOND_QUEEN") String DIAMOND_QUEEN();

    @DefaultStringValue("HEART_QUEEN") String HEART_QUEEN();

    @DefaultStringValue("SPADE_KING") String SPADE_KING();

    @DefaultStringValue("CLUB_KING") String CLUB_KING();

    @DefaultStringValue("DIAMOND_KING") String DIAMOND_KING();

    @DefaultStringValue("HEART_KING") String HEART_KING();

    @DefaultStringValue("SPADE_ACE") String SPADE_ACE();

    @DefaultStringValue("CLUB_ACE") String CLUB_ACE();

    @DefaultStringValue("DIAMOND_ACE") String DIAMOND_ACE();

    @DefaultStringValue("HEART_ACE") String HEART_ACE();

    @DefaultStringValue("Preferanser") String preferanser();

    @DefaultStringValue("Description") String description();

    @DefaultStringValue("Create new deal description") String createDealDescription();

    @DefaultStringValue("Deal description") String dealDescription();

    @DefaultStringValue("Deal name") String dealName();

    @DefaultStringValue("Deal name not specified") String dealNameNotSpecified();

    @DefaultStringValue("Deal editor") String editor();

    @DefaultStringValue("Вход") String login();

    @DefaultStringValue("Выход") String logout();

    @DefaultStringValue("Properties") String properties();

    @DefaultStringValue("To sluff") String sluff();

    @DefaultStringValue("Make turn from widow") String turnWidow();

    @DefaultStringValue("Undo") String undo();

    @DefaultStringValue("Redo") String redo();

    @DefaultStringValue("Pass") String pass();

    @DefaultStringValue("Whist") String whist();

    @DefaultStringValue("Miser") String miser();

    @DefaultStringValue("No Trump") String noTrump();

    @DefaultStringValue("East") String EAST();

    @DefaultStringValue("South") String SOUTH();

    @DefaultStringValue("West") String WEST();

    @DefaultStringValue("spade") String OF_SPADE();

    @DefaultStringValue("club") String OF_CLUB();

    @DefaultStringValue("diamond") String OF_DIAMOND();

    @DefaultStringValue("heart") String OF_HEART();

    @DefaultStringValue("♠") String SPADE_char();

    @DefaultStringValue("♣") String CLUB_char();

    @DefaultStringValue("♦") String DIAMOND_char();

    @DefaultStringValue("♥") String HEART_char();

    @DefaultStringValue("Play") String play();

    @DefaultStringValue("Enter") String enter();

    @DefaultStringValue("Game") String game();

    @DefaultStringValue("Widow") String widow();

    @DefaultStringValue("Edit") String edit();

    @DefaultStringValue("Reset") String reset();

    @DefaultStringValue("To sluff") String toSluff();

    @DefaultStringValue("New deal") String deal();

    @DefaultStringValue("Open") String open();

    @DefaultStringValue("Delete") String delete();

    @DefaultStringValue("Wait a second...") String waiting();

    @DefaultStringValue("Loaded") String loaded();

    @DefaultStringValue("saveDescription") String saveDescription();

    @DefaultStringValue("Выбрать контракт") String chooseContract();

    @DefaultStringValue("Закрыть") String close();

    @DefaultStringValue("Выйти") String quit();

    @DefaultStringValue("Сохранить") String save();

    @DefaultStringValue("Розыгрыши") String drawings();

    @DefaultStringValue("Сохранить розыгрыш") String saveDrawing();

    @DefaultStringValue("Сохранeние розыгрыша") String savingDrawing();

    @DefaultStringValue("Отменить") String cancel();

    @DefaultStringValue("Сохраняется...") String saving();

    @DefaultStringValue("Сохранено") String saved();

    @DefaultStringValue("Сохранить и разыграть") String saveAndPlay();

    @DefaultStringValue("Данный расклад не может быть разыгран, так как он содержит следующие ошибки:") String dealWithErrors();

    @DefaultStringValue("Создать расклад") String createDeal();

    @DefaultStringValue("Прикуп берёт взятки на распасах") String widowRaspassOption();

    @DefaultStringValue("Название розыгрыша") String drawingName();

    @DefaultStringValue("Описание розыгрыша") String drawingDesc();

    @DefaultStringValue("Ссылка") String link();
}
