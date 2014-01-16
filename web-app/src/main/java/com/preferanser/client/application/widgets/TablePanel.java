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

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.preferanser.client.application.mvp.editor.layout.*;
import com.preferanser.client.theme.greencloth.client.com.preferanser.client.application.PreferanserResources;
import com.preferanser.shared.domain.TableLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.preferanser.shared.domain.TableLocation.*;

public class TablePanel extends Composite {

    public interface Binder extends UiBinder<DockPanel, TablePanel> {}

    private static Binder uiBinder = GWT.create(Binder.class);

    protected PreferanserResources resources = GWT.create(PreferanserResources.class);

    @UiField HorizontalPanel eastPanelHeader;
    @UiField HorizontalPanel southPanelHeader;
    @UiField HorizontalPanel westPanelHeader;
    @UiField FlowPanel eastCardsPanel;
    @UiField FlowPanel southCardsPanel;
    @UiField FlowPanel westCardsPanel;
    @UiField TabPanel centerTabPanel;
    @UiField FlowPanel widowPanel;
    @UiField FlowPanel centerCardsPanel;

    private final BiMap<TableLocation, Panel> locationPanelMap = EnumHashBiMap.create(TableLocation.class);
    private final BiMap<TableLocation, Layout<CardWidget>> locationLayoutMap = EnumHashBiMap.create(TableLocation.class);

    private CenterLayout centerCardLayout;

    public TablePanel() {
        initWidget(uiBinder.createAndBindUi(this));

        locationPanelMap.put(EAST, eastCardsPanel);
        locationPanelMap.put(SOUTH, southCardsPanel);
        locationPanelMap.put(WEST, westCardsPanel);
        locationPanelMap.put(CENTER, centerCardsPanel);
        locationPanelMap.put(WIDOW, widowPanel);

        int cardWidth = resources.c7().getWidth();
        int cardHeight = resources.c7().getHeight();
        centerCardLayout = new CenterLayout(centerCardsPanel, cardWidth, cardHeight);
        locationLayoutMap.put(EAST, new EastLayout(eastCardsPanel, cardWidth, cardHeight));
        locationLayoutMap.put(SOUTH, new HorizontalLayout(southCardsPanel, cardWidth));
        locationLayoutMap.put(WEST, new WestLayout(westCardsPanel, cardWidth, cardHeight));
        locationLayoutMap.put(WIDOW, new WidowLayout(widowPanel, cardWidth, cardHeight));

        centerTabPanel.selectTab(0);
        centerTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                layoutLocation(TableLocation.CENTER);
                layoutLocation(TableLocation.WIDOW);
            }
        });
    }

    @UiChild
    public void addEastHeader(HasWidgets hasWidgets) {
        for (Widget widget : newArrayList(hasWidgets))
            eastPanelHeader.add(widget);
    }

    @UiChild
    public void addSouthHeader(HasWidgets hasWidgets) {
        for (Widget widget : newArrayList(hasWidgets))
            southPanelHeader.add(widget);
    }

    @UiChild
    public void addWestHeader(HasWidgets hasWidgets) {
        for (Widget widget : newArrayList(hasWidgets))
            westPanelHeader.add(widget);
    }

    @UiChild
    public void addCenter(HasWidgets hasWidgets) {
        for (Widget widget : newArrayList(hasWidgets))
            centerCardsPanel.add(widget);
    }

    public void addHandCardsToCenter(Collection<HandCard> handCards) {
        for (HandCard handCard : handCards)
            centerCardsPanel.add(handCard.getCardWidget());
        centerCardLayout.apply(handCards);
    }

    public void layoutLocation(final TableLocation location) {
        if (CENTER == location) {
            centerCardLayout.apply(newArrayList(filter(transform(centerCardsPanel, new Function<IsWidget, HandCard>() {
                @Nullable
                @Override
                public HandCard apply(@Nullable IsWidget widget) {
                    return widget instanceof HandCard ? (HandCard) widget : null;
                }
            }), Predicates.notNull())));
        } else {
            Panel panel = locationPanelMap.get(location);
            assert panel != null : "Panel for " + location + " is null";

            Layout<CardWidget> layout = locationLayoutMap.get(location);
            assert layout != null : "Layout for " + location + " is null";

            Collection<CardWidget> cardWidgets = newArrayList(filter(transform(panel, new Function<Widget, CardWidget>() {
                @Nullable
                @Override
                public CardWidget apply(@Nullable Widget widget) {
                    return widget instanceof CardWidget ? (CardWidget) widget : null;
                }
            }), Predicates.notNull()));

            layout.apply(cardWidgets);
        }
    }

    public void addCenterPanelClickHandler(ClickHandler clickHandler) {
        centerCardsPanel.addDomHandler(clickHandler, ClickEvent.getType());
    }

    public Map<Panel, TableLocation> getPanelLocations() {
        return ImmutableMap.of(
            (Panel) centerCardsPanel, CENTER,
            eastCardsPanel, EAST,
            westCardsPanel, WEST,
            southCardsPanel, SOUTH,
            widowPanel, WIDOW
        );
    }

    public Panel getLocationWidgetsContainer(TableLocation location) {
        switch (location) {
            case CENTER:
                return centerCardsPanel;
            case WEST:
                return westCardsPanel;
            case EAST:
                return eastCardsPanel;
            case SOUTH:
                return southCardsPanel;
            case WIDOW:
                return widowPanel;
            default:
                throw new IllegalArgumentException("There is no panel for the location: " + location);
        }
    }

}
