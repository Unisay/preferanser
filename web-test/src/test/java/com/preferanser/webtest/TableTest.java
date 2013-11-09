package com.preferanser.webtest;

import com.preferanser.webtest.steps.EndUserSteps;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;

public abstract class TableTest extends ThucydidesTest {

    private static final String TABLE_URL = "http://127.0.0.1:8888/Preferanser.html?gwt.codesvr=127.0.0.1:9997";

    @ManagedPages(defaultUrl = TABLE_URL)
    public Pages pages;

    @Steps
    public EndUserSteps endUser;
}
