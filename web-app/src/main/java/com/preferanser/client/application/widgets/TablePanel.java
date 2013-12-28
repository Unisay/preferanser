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
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.preferanser.client.application.game.editor.layout.*;
import com.preferanser.client.application.game.editor.style.TableStyle;
import com.preferanser.client.theme.greencloth.client.com.preferanser.client.application.PreferanserResources;
import com.preferanser.shared.domain.TableLocation;

import javax.annotation.Nullable;
import java.util.Collection;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.preferanser.shared.domain.TableLocation.*;

public class TablePanel extends Composite {

    public interface Binder extends UiBinder<VerticalPanel, TablePanel> {}

    private static Binder uiBinder = GWT.create(Binder.class);
    protected PreferanserResources resources = GWT.create(PreferanserResources.class);

    @UiField HorizontalPanel northPanelHeader;
    @UiField HorizontalPanel eastPanelHeader;
    @UiField HorizontalPanel southPanelHeader;
    @UiField HorizontalPanel westPanelHeader;
    @UiField HorizontalPanel centerPanelHeader;
    @UiField HorizontalPanel headerPanel;

    @UiField public FlowPanel northPanel;
    @UiField public FlowPanel eastPanel;
    @UiField public FlowPanel southPanel;
    @UiField public FlowPanel westPanel;
    @UiField public FlowPanel centerPanel;

    private TableStyle style;

    // TODO: consider replacing all public usages with methods
    public final BiMap<TableLocation, FlowPanel> locationPanelMap = EnumHashBiMap.create(TableLocation.class);
    private final BiMap<TableLocation, Layout<CardWidget>> locationLayoutMap = EnumHashBiMap.create(TableLocation.class);
    private CenterLayout centerCardLayout;

    public TablePanel(TableStyle style) {
        this.style = style;
        initWidget(uiBinder.createAndBindUi(this));

        locationPanelMap.put(NORTH, northPanel);
        locationPanelMap.put(EAST, eastPanel);
        locationPanelMap.put(SOUTH, southPanel);
        locationPanelMap.put(WEST, westPanel);
        locationPanelMap.put(CENTER, centerPanel);

        int cardWidth = resources.c7().getWidth();
        int cardHeight = resources.c7().getHeight();
        centerCardLayout = new CenterLayout(centerPanel, cardWidth, cardHeight);
        locationLayoutMap.put(NORTH, new HorizontalLayout(northPanel, cardWidth));
        locationLayoutMap.put(EAST, new EastLayout(eastPanel, cardWidth, cardHeight));
        locationLayoutMap.put(SOUTH, new HorizontalLayout(southPanel, cardWidth));
        locationLayoutMap.put(WEST, new WestLayout(westPanel, cardWidth, cardHeight));
    }

    @UiChild
    public void addHeader(HasWidgets hasWidgets) {
        for (Widget widget : newArrayList(hasWidgets))
            headerPanel.add(widget);
    }

    @UiChild
    public void addNorthHeader(HasWidgets hasWidgets) {
        for (Widget widget : newArrayList(hasWidgets))
            northPanelHeader.add(widget);
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
    public void addCenterHeader(HasWidgets hasWidgets) {
        for (Widget widget : newArrayList(hasWidgets))
            centerPanelHeader.add(widget);
    }

    @UiChild
    public void addCenter(HasWidgets hasWidgets) {
        for (Widget widget : newArrayList(hasWidgets))
            centerPanel.add(widget);
    }

    public void addCardinalCardsToCenter(Collection<CardinalCard> cardinalCards) {
        for (CardinalCard cardinalCard : cardinalCards)
            centerPanel.add(cardinalCard.getCardWidget());
        centerCardLayout.apply(cardinalCards);
    }

    public void layoutLocation(TableLocation location) {
        FlowPanel panel = locationPanelMap.get(location);
        Layout<CardWidget> layout = locationLayoutMap.get(location);
        Collection<CardWidget> cardWidgets = newArrayList(transform(panel, new Function<Widget, CardWidget>() {
            @Nullable @Override public CardWidget apply(@Nullable Widget widget) {
                if (widget instanceof CardWidget) {
                    return (CardWidget) widget;
                }
                return null;
            }
        }));
        layout.apply(cardWidgets);
    }

    public void addCenterPanelClickHandler(ClickHandler clickHandler) {
        centerPanel.addDomHandler(clickHandler, ClickEvent.getType());
    }

}
