package com.preferanser.client.application.table.layout;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.Collection;

public interface Layout<T extends IsWidget> {
    void apply(Collection<T> widgets);
}
