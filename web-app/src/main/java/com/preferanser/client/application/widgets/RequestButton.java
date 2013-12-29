package com.preferanser.client.application.widgets;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.preferanser.client.restygwt.RequestIdValue;
import com.preferanser.client.restygwt.RestyGwtDispatcher;
import com.preferanser.client.restygwt.RestyGwtRequestIdListenerAdapter;

public class RequestButton extends Button {

    public RequestButton(RestyGwtDispatcher restyGwtDispatcher, RequestIdValue requestIdValue, final String requestText, final String completeText) {
        restyGwtDispatcher.addRequestListener(new RestyGwtRequestIdListenerAdapter(requestIdValue) {

            public static final int MIN_SHOW_MILLIS = 1000;
            private String backupText = getText();
            public long showTime;

            @Override
            protected void beforeRequestIdSent(RequestIdValue requestIdValue) {
                backupText = getText();
                setText(requestText);
                setEnabled(false);
                showTime = System.currentTimeMillis();
            }

            @Override
            protected void afterResponseIdHandled(RequestIdValue requestIdValue) {
                restoreDelayed();
            }

            @Override
            protected void afterRequestIdErrorHandled(RequestIdValue requestIdValue) {
                restoreDelayed();
            }

            private void restoreDelayed() {
                long hideTime = System.currentTimeMillis();
                long shownMillis = hideTime - showTime;
                if (shownMillis < MIN_SHOW_MILLIS) {
                    new Timer() {
                        public void run() {
                            showCompleteText();
                        }
                    }.schedule((int) (MIN_SHOW_MILLIS - shownMillis));
                } else {
                    showCompleteText();
                }
            }

            private void showCompleteText() {
                setText(completeText);
                new Timer() {
                    public void run() {
                        setText(backupText);
                        setEnabled(true);
                    }
                }.schedule(MIN_SHOW_MILLIS);
            }

        });
    }

}
