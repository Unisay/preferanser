package com.preferanser.client.application.mvp.deal;

import com.gwtplatform.mvp.client.UiHandlers;
import com.preferanser.shared.domain.entity.Deal;

public interface DealUiHandlers extends UiHandlers {

    void playDeal(Deal deal);

    void deleteDeal(Deal deal);

    void openDealEditor();

}