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

package com.preferanser.client.application.widgets;

import com.google.common.base.Optional;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.game.editor.style.TableStyle;
import com.preferanser.domain.Contract;
import com.preferanser.domain.Suit;

/**
 * Contract link widget
 */
public class ContractLink extends Composite implements HasHTML, HasClickHandlers {

    private static final String DEBUG_ID_CONTRACT_LINK = "contract-link";
    private static final String DEBUG_ID_CONTRACT_LABEL = "contract-label";
    private final Hyperlink hyperlink;
    private final Label label;
    private final PreferanserConstants constants;
    private final TableStyle tableStyle;
    private Contract contract;
    private String defaultText;

    @UiConstructor
    public ContractLink(PreferanserConstants constants, TableStyle tableStyle) {
        this.constants = constants;
        this.tableStyle = tableStyle;
        defaultText = constants.chooseContract();
        label = GWT.create(Label.class);
        label.ensureDebugId(DEBUG_ID_CONTRACT_LABEL);
        String contractLabelStyle = tableStyle.contractLabel();
        label.setStylePrimaryName(contractLabelStyle);
        hyperlink = GWT.create(Hyperlink.class);
        hyperlink.ensureDebugId(DEBUG_ID_CONTRACT_LINK);
        hyperlink.setText(defaultText);
        hyperlink.setStylePrimaryName(tableStyle.contractLink());
        enable();
        Panel widgets = GWT.create(FlowPanel.class);
        widgets.add(hyperlink);
        widgets.add(label);
        initWidget(widgets);
    }

    public void setContract(Contract contract) {
        this.contract = contract;
        if (contract == null) {
            setHTML(defaultText);
        } else {
            switch (contract) {
                case PASS:
                    setText(constants.pass());
                    break;
                case WHIST:
                    setText(constants.whist());
                    break;
                case MISER:
                    setText(constants.miser());
                    break;
                default:
                    Element suitSpan = DOM.createSpan();
                    Element tricksSpan = DOM.createSpan();

                    Optional<Suit> maybeTrump = contract.getTrump();
                    if (!maybeTrump.isPresent()) {
                        suitSpan.addClassName(tableStyle.noTrump());
                        suitSpan.setInnerText(constants.noTrump());
                    } else {
                        Suit trump = maybeTrump.get();
                        suitSpan.addClassName(tableStyle.contractSuit());
                        suitSpan.addClassName(trump.name().toLowerCase());
                        suitSpan.setInnerText(constants.getString(trump.name() + "_char"));
                    }

                    tricksSpan.addClassName(tableStyle.contractTricks());
                    tricksSpan.setInnerText("" + contract.getTricksNumber());
                    setHTML(tricksSpan.getString() + " " + suitSpan.getString());
                    break;
            }
        }
    }

    public void disable() {
        if (contract != null)
            label.setText(" — " + hyperlink.getText().replace(" ", ""));
        else
            label.setText("");
        hyperlink.setVisible(false);
        label.setVisible(true);
    }

    public void enable() {
        hyperlink.setVisible(true);
        label.setVisible(false);
    }

    @Override public String getText() {
        return hyperlink.getText();
    }

    @Override public void setText(String text) {
        hyperlink.setText(text);
    }

    @Override public String getHTML() {
        return hyperlink.getHTML();
    }

    @Override public void setHTML(String html) {
        hyperlink.setHTML(html);
    }

    @Override public HandlerRegistration addClickHandler(ClickHandler handler) {
        return hyperlink.addHandler(handler, ClickEvent.getType());
    }

}
