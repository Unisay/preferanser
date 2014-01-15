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

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.preferanser.shared.domain.Contract;
import com.preferanser.shared.domain.Suit;

public class I18nHelper {

    private PreferanserConstants constants;

    @Inject
    public I18nHelper(PreferanserConstants constants) {
        this.constants = constants;
    }

    public String getContractName(Contract contract) {
        switch (contract) {
            case PASS:
                return constants.pass();
            case WHIST:
                return constants.whist();
            case MISER:
                return constants.miser();
            default:
                String suit;
                Optional<Suit> maybeTrump = contract.getTrump();
                if (!maybeTrump.isPresent()) {
                    suit = constants.noTrump();
                } else {
                    Suit trump = maybeTrump.get();
                    suit = constants.getString("OF_" + trump.name());
                }
                return contract.getTricksNumber() + " " + suit;
        }
    }

}
