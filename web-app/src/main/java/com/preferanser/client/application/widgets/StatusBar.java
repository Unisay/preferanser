package com.preferanser.client.application.widgets;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.preferanser.client.theme.greencloth.client.com.preferanser.client.application.PreferanserResources;

public class StatusBar extends Composite {

    public static final long MIN_SHOW_MILLIS = 1000;

    private final FlowPanel panel;
    private final Label label;
    private long showTime;

    public StatusBar(PreferanserResources.Style style) {
        label = new Label();

        panel = new FlowPanel();
        panel.setVisible(false);
        panel.addStyleName(style.statusPanel());
        panel.add(label);

        initWidget(panel);
    }

    public void showStatus(String status) {
        label.setText(status);
        panel.setVisible(true);
        showTime = System.currentTimeMillis();
    }

    public void hideStatus() {
        long hideTime = System.currentTimeMillis();
        long shownMillis = hideTime - showTime;
        if (shownMillis < MIN_SHOW_MILLIS) {
            new Timer() {
                public void run() {
                    panel.setVisible(false);
                }
            }.schedule((int) (MIN_SHOW_MILLIS - shownMillis));
        } else {
            panel.setVisible(false);
        }
    }

}
