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

import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.shared.domain.Card;

import java.util.Set;

public class HasDuplicateCardsValidationError extends GameBuilderValidationError {

    private Set<Card> duplicateCards;

    @SuppressWarnings("unused")
    public HasDuplicateCardsValidationError() {
        // for serialization
    }

    public HasDuplicateCardsValidationError(Set<Card> duplicateCards) {
        this.duplicateCards = duplicateCards;
    }

    @Override public String formatLocalMessage(PreferanserConstants constants) {
        return constants.HAS_DUPLICATE_CARDS(); // TODO display dup cards
    }
}
