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
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.shared.domain.entity.Deal;

import java.util.List;

public class DealView extends ViewWithUiHandlers<DealUiHandlers> implements DealPresenter.DealView {

    public interface Binder extends UiBinder<Widget, DealView> {}

    interface DealViewStyle extends CssResource {
        String odd();

        String deals();
    }

    private static final DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);

    @UiField(provided = true) FlexTable dealTable;
    @UiField DealViewStyle style;

    private final PreferanserConstants constants;

    @Inject
    public DealView(Binder uiBinder, PreferanserConstants constants) {
        this.constants = constants;
        dealTable = new FlexTable();
        HTMLTable.ColumnFormatter columnFormatter = dealTable.getColumnFormatter();
        columnFormatter.setWidth(1, "40px");
        columnFormatter.setWidth(2, "36px");
        columnFormatter.setWidth(3, "36px");
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override public void displayDeals(List<Deal> deals) {
        dealTable.removeAllRows();
        for (int i = 0; i < deals.size(); i++) {
            Deal deal = deals.get(i);
            if (i % 2 == 0)
                dealTable.getRowFormatter().addStyleName(i, style.odd());
            dealTable.setWidget(i, 0, new Label(deal.getName()));
            dealTable.setWidget(i, 1, createDateTimeLabel(deal));
            dealTable.setWidget(i, 2, createPlayButton(deal));
            dealTable.setWidget(i, 3, createDeleteButton(deal));
        }
    }

    private Label createDateTimeLabel(Deal deal) {
        Label label = new Label(DATE_TIME_FORMAT.format(deal.getCreated()));
        label.setWordWrap(false);
        return label;
    }

    private Button createPlayButton(final Deal deal) {
        Button button = new Button(constants.play());
        button.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                getUiHandlers().playDeal(deal);
            }
        });
        return button;
    }

    private Button createDeleteButton(final Deal deal) {
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
