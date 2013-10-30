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

package com.preferanser.client.application.table.dialog.contract;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.i18n.PreferanserMessages;
import com.preferanser.shared.Cardinal;
import com.preferanser.shared.Contract;

public class ContractDialogView extends PopupViewWithUiHandlers<ContractDialogUiHandlers> implements ContractDialogPresenter.MyView {


    interface Binder extends UiBinder<PopupPanel, ContractDialogView> {}

    @UiField DialogBox dialog;
    @UiField PreferanserConstants constants;
    @UiField PreferanserMessages messages;

    @Inject
    protected ContractDialogView(Binder uiBinder, EventBus eventBus) {
        super(eventBus);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setCardinal(Cardinal cardinal) {
        dialog.setText(messages.cardinalChoosesContract(constants.getString(cardinal.name())));
    }

    @UiHandler("sixSpadeButton") void onSixSpadeClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.SIX_SPADE);
    }

    @UiHandler("sixClubButton") void onSixClubClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.SIX_CLUB);
    }

    @UiHandler("sixDiamondButton") void onSixDiamondClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.SIX_DIAMOND);
    }

    @UiHandler("sixHeartButton") void onSixHeartClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.SIX_HEART);
    }

    @UiHandler("sixNoTrumpButton") void onSixNoTrumpClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.SIX_NO_TRUMP);
    }

    @UiHandler("sevenSpadeButton") void onSevenSpadeClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.SEVEN_SPADE);
    }

    @UiHandler("sevenClubButton") void onSevenClubClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.SEVEN_CLUB);
    }

    @UiHandler("sevenDiamondButton") void onSevenDiamondClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.SEVEN_DIAMOND);
    }

    @UiHandler("sevenHeartButton") void onSevenHeartClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.SEVEN_HEART);
    }

    @UiHandler("sevenNoTrumpButton") void onSevenNoTrumpClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.SEVEN_NO_TRUMP);
    }

    @UiHandler("eightSpadeButton") void onEightSpadeClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.EIGHT_SPADE);
    }

    @UiHandler("eightClubButton") void onEightClubClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.EIGHT_CLUB);
    }

    @UiHandler("eightDiamondButton") void onEightDiamondClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.EIGHT_DIAMOND);
    }

    @UiHandler("eightHeartButton") void onEightHeartClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.EIGHT_HEART);
    }

    @UiHandler("eightNoTrumpButton") void onEightNoTrumpClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.EIGHT_NO_TRUMP);
    }

    @UiHandler("nineSpadeButton") void onNineSpadeClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.NINE_SPADE);
    }

    @UiHandler("nineClubButton") void onNineClubClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.NINE_CLUB);
    }

    @UiHandler("nineDiamondButton") void onNineDiamondClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.NINE_DIAMOND);
    }

    @UiHandler("nineHeartButton") void onNineHeartClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.NINE_HEART);
    }

    @UiHandler("nineNoTrumpButton") void onNineNoTrumpClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.NINE_NO_TRUMP);
    }

    @UiHandler("tenSpadeButton") void onTenSpadeClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.TEN_SPADE);
    }

    @UiHandler("tenClubButton") void onTenClubClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.TEN_CLUB);
    }

    @UiHandler("tenDiamondButton") void onTenDiamondClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.TEN_DIAMOND);
    }

    @UiHandler("tenHeartButton") void onTenHeartClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.TEN_HEART);
    }

    @UiHandler("tenNoTrumpButton") void onTenNoTrumpClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.TEN_NO_TRUMP);
    }

    @UiHandler("passButton") void onPassClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.PASS);
    }

    @UiHandler("whistButton") void onWhistClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.WHIST);
    }

    @UiHandler("miserButton") void onMiserClicked(@SuppressWarnings("unused") ClickEvent event) {
        handleContractButton(Contract.MISER);
    }

    private void handleContractButton(Contract contract) {
        if (getUiHandlers().setContract(contract)) {
            hide();
        }
    }

}
