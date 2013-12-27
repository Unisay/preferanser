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

package com.preferanser.client.application.game;

import com.gwtplatform.mvp.client.View;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.Cardinal;
import com.preferanser.shared.domain.Contract;
import com.preferanser.shared.domain.TableLocation;

import java.util.Collection;
import java.util.Map;

public interface TableView extends View {

    void displayTurn(Cardinal turn);
    void displayContracts(Map<Cardinal, Contract> cardinalContracts);
    void displayCardinalTricks(Map<Cardinal, Integer> cardinalTricks);
    void displayTableCards(Map<TableLocation, Collection<Card>> tableCards, Map<Card, Cardinal> centerCards);

}
