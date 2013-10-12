package com.preferanser.client.application.table;

import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Selector;
import com.google.gwt.query.client.Selectors;

public interface GQuerySelectors extends Selectors {

    @Selector("div, img") GQuery getAllDivsAndImages();
    @Selector(".handPanel") GQuery getAllHandPanels();

}