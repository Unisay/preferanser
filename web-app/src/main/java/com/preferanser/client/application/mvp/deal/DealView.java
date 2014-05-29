package com.preferanser.client.application.mvp.deal;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.gwtp.NameTokens;
import com.preferanser.shared.domain.DealInfo;

import java.util.Date;
import java.util.List;

public class DealView extends ViewWithUiHandlers<DealUiHandlers> implements DealPresenter.DealView {

    private static final int COLUMN_NAME = 0;
    private static final int COLUMN_TIME = 1;
    private static final int COLUMN_EDIT = 2;
    private static final int COLUMN_DELETE = 3;

    public interface Binder extends UiBinder<Widget, DealView> {}

    interface DealViewStyle extends CssResource {
        String odd();
        String deals();
        String link();
    }

    private static final DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);

    @UiField(provided = true) FlexTable dealTable;
    @UiField DealViewStyle style;

    private final PreferanserConstants constants;
    private final PlaceManager placeManager;

    @Inject
    public DealView(Binder uiBinder, PreferanserConstants constants, PlaceManager placeManager) {
        this.constants = constants;
        this.placeManager = placeManager;
        dealTable = new FlexTable();
        HTMLTable.ColumnFormatter columnFormatter = dealTable.getColumnFormatter();
        columnFormatter.setWidth(COLUMN_TIME, "40px");
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override public void displayDeals(List<DealInfo> deals, boolean allowModifications) {
        if (allowModifications) {
            HTMLTable.ColumnFormatter formatter = dealTable.getColumnFormatter();
            formatter.setWidth(COLUMN_EDIT, "36px");
            formatter.setWidth(COLUMN_DELETE, "36px");
        }

        dealTable.removeAllRows();
        for (int i = 0; i < deals.size(); i++) {
            DealInfo deal = deals.get(i);
            if (i % 2 == 0)
                dealTable.getRowFormatter().addStyleName(i, style.odd());
            dealTable.setWidget(i, COLUMN_NAME, createDealLink(deal));
            dealTable.setWidget(i, COLUMN_TIME, createDateTimeLabel(deal));
            if (allowModifications) {
                dealTable.setWidget(i, COLUMN_EDIT, createEditButton(deal));
                dealTable.setWidget(i, COLUMN_DELETE, createDeleteButton(deal));
            }
        }
    }

    private Hyperlink createDealLink(DealInfo dealInfo) {
        PlaceRequest placeRequest = new PlaceRequest.Builder()
            .nameToken(NameTokens.PLAYER)
            .with("user", dealInfo.getOwnerId().toString())
            .with("deal", Long.toString(dealInfo.getId()))
            .build();
        Hyperlink hyperlink = new Hyperlink(dealInfo.getName(), placeManager.buildHistoryToken(placeRequest));
        hyperlink.addStyleName(style.link());
        return hyperlink;
    }

    private Label createDateTimeLabel(DealInfo deal) {
        Date created = deal.getCreated();
        Label label = new Label(created == null ? "" : DATE_TIME_FORMAT.format(created));
        label.setWordWrap(false);
        return label;
    }

    private Button createEditButton(final DealInfo deal) {
        Button button = new Button(constants.edit());
        button.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                getUiHandlers().editDeal(deal);
            }
        });
        return button;
    }

    private Button createDeleteButton(final DealInfo deal) {
        final Button button = new Button(constants.delete());
        button.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                button.setEnabled(false);
                getUiHandlers().deleteDeal(deal);
            }
        });
        return button;
    }

}
