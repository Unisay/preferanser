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

package com.preferanser.shared.domain.exception.validation;

import com.google.common.base.Joiner;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.i18n.PreferanserMessages;
import com.preferanser.shared.domain.Cardinal;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class WrongNumCardsPerCardinalValidationError extends GameBuilderValidationError {

    private Map<Cardinal, Integer> wrongCardinals;

    @SuppressWarnings("unused")
    public WrongNumCardsPerCardinalValidationError() {
        // for serialization
    }

    public WrongNumCardsPerCardinalValidationError(Map<Cardinal, Integer> wrongCardinals) {
        this.wrongCardinals = wrongCardinals;
    }

    @Override public String formatLocalMessage(PreferanserConstants constants, PreferanserMessages messages) {
        List<String> params = newArrayList();
        for (Map.Entry<Cardinal, Integer> entry : wrongCardinals.entrySet())
            params.add(constants.getString(entry.getKey().name()).toLowerCase() + " ‒ " + entry.getValue());
        return messages.wrongNumCardsPerCardinal(Joiner.on(", ").join(params));
    }

}
