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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that uses the HTML UL element. All children will be wrapped into LI elements.
 * <p/>
 * Using UL lists is a modern pattern to layout web pages, as it is easy to style them
 * with CSS. Moreover, they have several advantages over tables (changing the layout
 * requires changing the code, accessibility, etc).
 */
public class UlListPanel extends ComplexPanel implements InsertPanel {

    /**
     * Creates an empty flow panel.
     */
    public UlListPanel() {
        setElement(Document.get().createULElement());
    }

    private LiPanel wrapWidget(Widget w) {
        LiPanel liPanel = new LiPanel();
        liPanel.add(w);
        return liPanel;
    }

    /**
     * Adds a new child widget to the panel.
     *
     * @param w the widget to be added
     */
    @Override
    public void add(Widget w) {
        add(wrapWidget(w), getElement());
    }

    @Override
    public void clear() {
        // Remove all existing child nodes.
        Node child = getElement().getFirstChild();
        while (child != null) {
            getElement().removeChild(child);
            child = getElement().getFirstChild();
        }
    }

    /**
     * Inserts a widget before the specified index.
     *
     * @param w           the widget to be inserted
     * @param beforeIndex the index before which it will be inserted
     * @throws IndexOutOfBoundsException if <code>beforeIndex</code> is out of range
     */
    public void insert(Widget w, int beforeIndex) {
        insert(wrapWidget(w), getElement(), beforeIndex, true);
    }

    /**
     * The LI element for use in {@link UlListPanel}s.
     */
    private static class LiPanel extends ComplexPanel implements InsertPanel {

        protected LiPanel() {
            setElement(Document.get().createLIElement());
        }

        /**
         * Adds a new child widget to the panel.
         *
         * @param w the widget to be added
         */
        @Override
        public void add(Widget w) {
            add(w, getElement());
        }

        @Override
        public void clear() {
            // Remove all existing child nodes.
            Node child = getElement().getFirstChild();
            while (child != null) {
                getElement().removeChild(child);
                child = getElement().getFirstChild();
            }
        }

        /**
         * Inserts a widget before the specified index.
         *
         * @param w           the widget to be inserted
         * @param beforeIndex the index before which it will be inserted
         * @throws IndexOutOfBoundsException if <code>beforeIndex</code> is out of range
         */
        public void insert(Widget w, int beforeIndex) {
            insert(w, getElement(), beforeIndex, true);
        }
    }
}