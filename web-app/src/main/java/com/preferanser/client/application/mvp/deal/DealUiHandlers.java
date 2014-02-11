package com.preferanser.client.application.mvp.deal;

import com.gwtplatform.mvp.client.UiHandlers;
import com.preferanser.shared.domain.DealInfo;

public interface DealUiHandlers extends UiHandlers {

    void playDeal(DealInfo deal);

    void editDeal(DealInfo deal);

    void deleteDeal(DealInfo deal);

}