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

import com.google.gwt.i18n.client.Messages;

/**
 * Preferanser i18n messages
 * <p/>
 * See <a href="http://www.gwtproject.org/doc/latest/DevGuideI18n.html">GWT i18n doc</a>
 */
public interface PreferanserMessages extends Messages {
    @DefaultMessage("{0} chooses contract") String cardinalChoosesContract(String who);
    @DefaultMessage("Deal") String deal();
    @DefaultMessage("Wrong number of contracts") String wrongNumberOfContracts();
    @DefaultMessage("There are conflicting contracts") String hasConflictingContracts();
    @DefaultMessage("Fist turn not specified") String firstTurnNotSpecified();
    @DefaultMessage("First turn specified incorrectly") String wrongFirstTurn();
    @DefaultMessage("Cards dealt incorrectly: {0}") String wrongNumCardsPerCardinal(String join);
    @DefaultMessage("Number of players not specified") String numPlayersNotSpecified();
    @DefaultMessage("Has duplicate cards: {0}") String hasDuplicateCards(String join);
}
