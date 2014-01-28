package com.preferanser.server.dao;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class DatastoreTestNGListener extends TestListenerAdapter {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Override public void onTestStart(ITestResult result) {
        helper.setUp();
    }

    @Override public void onTestSuccess(ITestResult result) {
        helper.tearDown();
    }

    @Override public void onTestFailure(ITestResult result) {
        helper.tearDown();
    }

}
