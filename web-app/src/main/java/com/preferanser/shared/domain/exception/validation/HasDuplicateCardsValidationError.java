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
import com.preferanser.shared.domain.Card;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

public class HasDuplicateCardsValidationError extends GameBuilderValidationError {

    private Set<Card> duplicateCards;

    @SuppressWarnings("unused")
    public HasDuplicateCardsValidationError() {
        // for serialization
    }

    public HasDuplicateCardsValidationError(Set<Card> duplicateCards) {
        this.duplicateCards = duplicateCards;
    }

    @Override public String formatLocalMessage(PreferanserConstants constants, PreferanserMessages messages) {
        List<String> cardNames = newArrayList();
        for (Card card : duplicateCards)
            cardNames.add(constants.getString(card.name()));
        return messages.hasDuplicateCards(Joiner.on(", ").join(cardNames));
    }

}
