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

package com.preferanser.domain.exception;

import com.preferanser.domain.Card;
import com.preferanser.domain.Cardinal;

import java.util.Map;

public class DuplicateGameTurnException extends GameTurnException {

    private Map<Card, Cardinal> centerCardCardinalMap;
    private Cardinal fromCardinal;

    @SuppressWarnings("unused") // required for serialization
    public DuplicateGameTurnException() {
    }

    public DuplicateGameTurnException(Map<Card, Cardinal> centerCardCardinalMap, Cardinal fromCardinal) {
        this.centerCardCardinalMap = centerCardCardinalMap;
        this.fromCardinal = fromCardinal;
    }

    @Override public String getMessage() {
        return getFromCardinal() + " made its turn already: " + getCenterCardCardinalMap();
    }

    public Map<Card, Cardinal> getCenterCardCardinalMap() {
        return centerCardCardinalMap;
    }

    public Cardinal getFromCardinal() {
        return fromCardinal;
    }
}
