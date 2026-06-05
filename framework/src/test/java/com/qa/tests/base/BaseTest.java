package com.qa.tests.base;

import com.qa.framework.config.ConfigManager;
import com.qa.framework.utils.ReportManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * BaseTest — parent class for all test classes.
 *
 * Every test class extends BaseTest.
 * This handles:
 *   - RestAssured base URL setup (from config.properties)
 *   - Common request headers and timeouts
 *   - ExtentReport init and flush
 *   - Request/response logging for debugging
 */
public class BaseTest {

    // Shared request spec — all tests use this as the starting point
    protected static RequestSpecification requestSpec;

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        System.out.println("========================================");
        System.out.println(" REST API Framework — Test Suite Start  ");
        System.out.println("========================================");

        // Initialize the HTML report
        ReportManager.initReport();

        // Set base URL from config.properties
        RestAssured.baseURI = ConfigManager.get("BASE_URL");

        // Build a reusable request spec
        // All tests inherit: base URL, content type, timeouts, logging
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.get("BASE_URL"))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(
                    io.restassured.config.RestAssuredConfig.config()
                        .connectionConfig(
                            io.restassured.config.ConnectionConfig.connectionConfig()
                                .closeIdleConnectionsAfterEachResponse()
                        )
                )
                // Logs every request and response to console — great for debugging
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();

        System.out.println("[BaseTest] Base URL set to: " + ConfigManager.get("BASE_URL"));
        System.out.println("[BaseTest] Suite setup complete.");
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        // Write the HTML report to disk
        ReportManager.flushReport();

        System.out.println("========================================");
        System.out.println(" Test Suite Complete — Report saved.    ");
        System.out.println("[BaseTest] Check: " + ConfigManager.get("REPORT_PATH", "test-output/ExtentReport.html"));
        System.out.println("========================================");
    }

}
