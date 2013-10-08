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

package com.preferanser.client.application.table;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.preferanser.client.application.PreferanserResources;
import com.preferanser.server.business.Card;

import java.util.HashMap;
import java.util.Map;

/**
 * Table view
 */
public class TableView extends ViewWithUiHandlers<TableUiHandlers> implements TablePresenter.TableView {

    public interface Binder extends UiBinder<Widget, TableView> {}

    @UiField(provided = true) final PreferanserResources resources;

    private Map<Card, Image> cards = new HashMap<Card, Image>(32);

    @Inject
    public TableView(Binder uiBinder, PreferanserResources resources) {
        this.resources = resources;
        initWidget(uiBinder.createAndBindUi(this));

        cards.put(Card.CLUB_SEVEN, c7);
        cards.put(Card.SPADE_SEVEN, s7);
        cards.put(Card.DIAMOND_SEVEN, d7);
        cards.put(Card.HEART_SEVEN, h7);
        cards.put(Card.CLUB_EIGHT, c8);
        cards.put(Card.SPADE_EIGHT, s8);
        cards.put(Card.DIAMOND_EIGHT, d8);
        cards.put(Card.HEART_EIGHT, h8);
        cards.put(Card.CLUB_NINE, c9);
        cards.put(Card.SPADE_NINE, s9);
        cards.put(Card.DIAMOND_NINE, d9);
        cards.put(Card.HEART_NINE, h9);
        cards.put(Card.CLUB_TEN, c10);
        cards.put(Card.SPADE_TEN, s10);
        cards.put(Card.DIAMOND_TEN, d10);
        cards.put(Card.HEART_TEN, h10);
        cards.put(Card.CLUB_JACK, cj);
        cards.put(Card.SPADE_JACK, sj);
        cards.put(Card.DIAMOND_JACK, dj);
        cards.put(Card.HEART_JACK, hj);
        cards.put(Card.CLUB_QUEEN, cq);
        cards.put(Card.SPADE_QUEEN, sq);
        cards.put(Card.DIAMOND_QUEEN, dq);
        cards.put(Card.HEART_QUEEN, hq);
        cards.put(Card.CLUB_KING, ck);
        cards.put(Card.SPADE_KING, sk);
        cards.put(Card.DIAMOND_KING, dk);
        cards.put(Card.HEART_KING, hk);
        cards.put(Card.CLUB_ACE, ca);
        cards.put(Card.SPADE_ACE, sa);
        cards.put(Card.DIAMOND_ACE, da);
        cards.put(Card.HEART_ACE, ha);
    }

    @UiField Image c7;
    @UiField Image s7;
    @UiField Image d7;
    @UiField Image h7;
    @UiField Image c8;
    @UiField Image s8;
    @UiField Image d8;
    @UiField Image h8;
    @UiField Image c9;
    @UiField Image s9;
    @UiField Image d9;
    @UiField Image h9;
    @UiField Image c10;
    @UiField Image s10;
    @UiField Image d10;
    @UiField Image h10;
    @UiField Image cj;
    @UiField Image sj;
    @UiField Image dj;
    @UiField Image hj;
    @UiField Image cq;
    @UiField Image sq;
    @UiField Image dq;
    @UiField Image hq;
    @UiField Image ck;
    @UiField Image sk;
    @UiField Image dk;
    @UiField Image hk;
    @UiField Image ca;
    @UiField Image sa;
    @UiField Image da;
    @UiField Image ha;
}
