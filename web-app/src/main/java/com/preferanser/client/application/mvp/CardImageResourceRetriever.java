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

package com.preferanser.client.application.mvp;

import com.google.common.base.Preconditions;
import com.google.gwt.resources.client.ImageResource;
import com.preferanser.client.theme.greencloth.client.com.preferanser.client.application.PreferanserResources;
import com.preferanser.shared.domain.Card;

/**
 * Returns image resource by card.
 */
class CardImageResourceRetriever {

    private PreferanserResources resources;

    CardImageResourceRetriever(PreferanserResources resources) {
        Preconditions.checkNotNull(resources, "PreferanserResources object is null");
        this.resources = resources;
    }

    ImageResource getByCard(Card card) {
        switch (card) {
            case SPADE_SEVEN:
                return resources.s7();

            case CLUB_SEVEN:
                return resources.c7();

            case DIAMOND_SEVEN:
                return resources.d7();

            case HEART_SEVEN:
                return resources.h7();


            case SPADE_EIGHT:
                return resources.s8();

            case CLUB_EIGHT:
                return resources.c8();

            case DIAMOND_EIGHT:
                return resources.d8();

            case HEART_EIGHT:
                return resources.h8();


            case SPADE_NINE:
                return resources.s9();

            case CLUB_NINE:
                return resources.c9();

            case DIAMOND_NINE:
                return resources.d9();

            case HEART_NINE:
                return resources.h9();


            case SPADE_TEN:
                return resources.s10();

            case CLUB_TEN:
                return resources.c10();

            case DIAMOND_TEN:
                return resources.d10();

            case HEART_TEN:
                return resources.h10();


            case SPADE_JACK:
                return resources.sj();

            case CLUB_JACK:
                return resources.cj();

            case DIAMOND_JACK:
                return resources.dj();

            case HEART_JACK:
                return resources.hj();


            case SPADE_QUEEN:
                return resources.sq();

            case CLUB_QUEEN:
                return resources.cq();

            case DIAMOND_QUEEN:
                return resources.dq();

            case HEART_QUEEN:
                return resources.hq();


            case SPADE_KING:
                return resources.sk();

            case CLUB_KING:
                return resources.ck();

            case DIAMOND_KING:
                return resources.dk();

            case HEART_KING:
                return resources.hk();


            case SPADE_ACE:
                return resources.sa();

            case CLUB_ACE:
                return resources.ca();

            case DIAMOND_ACE:
                return resources.da();

            case HEART_ACE:
                return resources.ha();

            default:
                throw new IllegalArgumentException("Don't have an ImageResource for the card = " + card);
        }
    }


}
