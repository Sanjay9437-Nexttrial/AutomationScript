package Utility;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import base.BaseClass;

public class ExtentReportManager implements ITestListener {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    ExtentSparkReporter sparkReporter;
    String repName;

    @Override
    public void onStart(ITestContext context) {

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        repName = "Automation-Report-" + timeStamp + ".html";

        sparkReporter = new ExtentSparkReporter("./reports/" + repName);

        // UI Config
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("Automation Test Results");
        sparkReporter.config().setReportName("Selenium Framework Execution");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // System Info
        extent.setSystemInfo("Executed By", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", context.getCurrentXmlTest().getParameter("env"));
        extent.setSystemInfo("Browser", context.getCurrentXmlTest().getParameter("browser"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));

        List<String> groups = context.getCurrentXmlTest().getIncludedGroups();
        if (!groups.isEmpty()) {
            extent.setSystemInfo("Groups", groups.toString());
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName())
                .assignCategory(result.getMethod().getGroups());
        test.set(extentTest);

        test.get().log(Status.INFO,
                MarkupHelper.createLabel("Starting Test: " + result.getMethod().getMethodName(), ExtentColor.BLUE));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, 
                MarkupHelper.createLabel("PASSED: " + result.getMethod().getMethodName(), ExtentColor.GREEN));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL,
                MarkupHelper.createLabel("FAILED: " + result.getMethod().getMethodName(), ExtentColor.RED));

        test.get().log(Status.INFO, result.getThrowable());

        try {
            String screenshotPath = new BaseClass().captureScreen(result.getMethod().getMethodName());
            test.get().addScreenCaptureFromPath(screenshotPath);
        } catch (Exception e) {
            test.get().log(Status.WARNING, "Screenshot could not be attached.");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP,
                MarkupHelper.createLabel("SKIPPED: " + result.getMethod().getMethodName(), ExtentColor.ORANGE));

        if (result.getThrowable() != null) {
            test.get().log(Status.INFO, result.getThrowable());
        }
    }

    @Override
    public void onFinish(ITestContext context) {

        extent.flush();

        File htmlReport = new File(System.getProperty("user.dir") + "/reports/" + repName);

        try {
            Desktop.getDesktop().browse(htmlReport.toURI());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
