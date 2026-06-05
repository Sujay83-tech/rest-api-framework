package com.qa.framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.qa.framework.config.ConfigManager;

/**
 * ReportManager — creates and manages the ExtentReports HTML report.
 * BaseTest calls initReport() before tests and flushReport() after.
 * Individual tests call getTest() to log steps.
 */
public class ReportManager {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    /**
     * Call once before the test suite starts.
     * Creates the HTML report file.
     */
    public static void initReport() {
        String reportPath = ConfigManager.get("REPORT_PATH", "test-output/ExtentReport.html");
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);

        spark.config().setDocumentTitle(ConfigManager.get("REPORT_TITLE", "API Test Report"));
        spark.config().setReportName(ConfigManager.get("REPORT_NAME", "API Automation Suite"));
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setEncoding("UTF-8");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Base URL", ConfigManager.get("BASE_URL"));
        extent.setSystemInfo("Environment", ConfigManager.get("ENV", "dev"));
        extent.setSystemInfo("Tester", "Sujay");
        extent.setSystemInfo("Framework", "Java + RestAssured + TestNG");

        System.out.println("[ReportManager] Report will be saved to: " + reportPath);
    }

    /**
     * Creates a new test entry in the report.
     * Call at the start of each @Test method.
     */
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = extent.createTest(testName, description);
        testThread.set(test);
        return test;
    }

    /**
     * Gets the current test's logger.
     * Call this anywhere in your test to log steps.
     */
    public static ExtentTest getTest() {
        return testThread.get();
    }

    /**
     * Call once after all tests complete.
     * Writes the report to disk.
     */
    public static void flushReport() {
        if (extent != null) {
            extent.flush();
            System.out.println("[ReportManager] Report flushed successfully.");
        }
    }

}
