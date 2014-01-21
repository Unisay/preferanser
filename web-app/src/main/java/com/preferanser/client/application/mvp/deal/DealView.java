package com.preferanser.client.application.mvp.deal;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.laf.client.PreferanserResources;
import com.preferanser.shared.domain.entity.Deal;

import java.util.List;

public class DealView extends ViewWithUiHandlers<DealUiHandlers> implements DealPresenter.DealView {

    public interface Binder extends UiBinder<Widget, DealView> {}

    @UiField(provided = true) Grid dealGrid;
    @UiField PreferanserResources resources;
    @UiField PreferanserConstants constants;

    @Inject
    public DealView(Binder uiBinder) {
        dealGrid = new Grid(0, 3);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override public void displayDeals(List<Deal> deals) {
        dealGrid.resizeRows(dealGrid.getRowCount() + deals.size());
        for (int i = 0; i < deals.size(); i++) {
            Deal deal = deals.get(i);
            dealGrid.setWidget(i, 0, new Label(deal.getName()));
            dealGrid.setWidget(i, 1, new Label(deal.getCreated().toString()));
            dealGrid.setWidget(i, 2, new Button(constants.play()));
        }
    }

}
