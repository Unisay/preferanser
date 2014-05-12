package com.preferanser.testng;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class DatastoreTestNGListener extends TestListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatastoreTestNGListener.class);

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Override public void onTestStart(ITestResult result) {
        helper.setUp();
    }

    @Override public void onTestSuccess(ITestResult result) {
        shutdownLocalDatastore();
    }

    @Override public void onTestFailure(ITestResult result) {
        shutdownLocalDatastore();
    }

    private void shutdownLocalDatastore() {
        try {
            helper.tearDown();
        } catch (Exception e) {
            LOGGER.error("Exception in LocalServiceTestHelper::tearDown()", e);
        }
    }

}
