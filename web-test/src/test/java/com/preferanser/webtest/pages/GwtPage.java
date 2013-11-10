package com.preferanser.webtest.pages;

import com.google.common.base.Predicate;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class GwtPage extends PageObject {

    protected static final String GWT_DEBUG_PREFIX = "gwt-debug-";

    public GwtPage(WebDriver driver, Predicate<PageObject> callback) {
        super(driver, callback);
    }

    public GwtPage(WebDriver driver, int ajaxTimeout) {
        super(driver, ajaxTimeout);
    }

    public GwtPage(WebDriver driver) {
        super(driver);
    }

    protected WebElementFacade gwtElementById(String id) {
        return element(getGwtId(id));
    }

    protected By getGwtId(String id) {
        return By.id(GWT_DEBUG_PREFIX + id);
    }

}
