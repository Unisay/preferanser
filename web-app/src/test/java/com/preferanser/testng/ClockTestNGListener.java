package com.preferanser.testng;

import com.preferanser.shared.util.Clock;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.util.Date;

public class ClockTestNGListener extends TestListenerAdapter {

    @Override public void onTestStart(ITestResult result) {
        Clock.setNow(new Date(1));
    }

    @Override public void onTestSuccess(ITestResult result) {
        Clock.setNow(new Date());
    }

    @Override public void onTestFailure(ITestResult result) {
        Clock.setNow(new Date());
    }

}
