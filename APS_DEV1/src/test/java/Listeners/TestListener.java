package Listeners;

import Base.BaseClass;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        BaseClass base = (BaseClass) result.getInstance();
        try {
            base.captureScreen(result.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {}

    @Override
    public void onTestSuccess(ITestResult result) {
    	
    	  //Optional: capture screen for successful tests
    	 BaseClass base = (BaseClass) result.getInstance();
         try {
             base.captureScreen(result.getName() + "_PASSED");
         } catch (Exception e) {
             e.printStackTrace();
         }
    }

    @Override
    public void onTestSkipped(ITestResult result) {}

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    @Override
    public void onStart(ITestContext context) {}

    @Override
    public void onFinish(ITestContext context) {}
}
