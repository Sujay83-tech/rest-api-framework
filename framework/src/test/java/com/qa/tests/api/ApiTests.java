package com.qa.tests.api;

import com.qa.framework.utils.ReportManager;
import com.qa.tests.base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * ApiTests — functional tests for your actual API endpoints.
 *
 * HOW TO ADD TESTS:
 * As the dev team adds new APIs to the project, you add a new @Test
 * method here for each endpoint.
 *
 * The pattern is always the same:
 *   1. given()  → set up the request (headers, body, params)
 *   2. when()   → call the endpoint (GET, POST, PUT, DELETE)
 *   3. then()   → assert the response (status code, body content)
 *
 * ── Example tests below target the Render project's test API ──────────
 * Update the paths once you get the actual endpoint list from the devs.
 */
public class ApiTests extends BaseTest {

    // ── Test Pattern: GET request with status code check ──────────────────
    @Test(priority = 1,
          description = "GET /api/test — should return HTTP 200")
    public void testApiShouldReturn200() {
        ReportManager.createTest(
            "GET /api/test — Status Code",
            "Verify the test endpoint returns HTTP 200"
        );

        Response response = given()
                .spec(requestSpec)
            .when()
                .get("/api/test")          // ← update with your actual endpoint path
            .then()
                .extract().response();

        ReportManager.getTest().info("HTTP Status: " + response.getStatusCode());
        ReportManager.getTest().info("Response Body: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 200,
            "Expected HTTP 200 but got: " + response.getStatusCode());

        ReportManager.getTest().pass("GET /api/test returned HTTP 200 ✔");
    }

    // ── Test Pattern: GET with response time check ─────────────────────────
    @Test(priority = 2,
          description = "GET /api/test — response time should be under 5 seconds")
    public void testApiResponseTime() {
        ReportManager.createTest(
            "GET /api/test — Response Time",
            "Verify the test endpoint responds within 5 seconds (BFSI SLA)"
        );

        Response response = given()
                .spec(requestSpec)
            .when()
                .get("/api/test")
            .then()
                .extract().response();

        long responseTime = response.getTime();

        ReportManager.getTest().info("Response time: " + responseTime + " ms");

        Assert.assertTrue(responseTime < 5000,
            "Response time exceeded 5000ms SLA. Actual: " + responseTime + "ms");

        ReportManager.getTest().pass("Response time: " + responseTime + "ms — within SLA ✔");
    }

    // ── Test Pattern: POST with request body ──────────────────────────────
    // Uncomment and update when the devs add a POST endpoint
    /*
    @Test(priority = 3,
          description = "POST /api/resource — should return HTTP 201")
    public void createResourceShouldReturn201() {
        ReportManager.createTest(
            "POST /api/resource — Create",
            "Verify creating a resource returns HTTP 201"
        );

        String requestBody = "{ \"name\": \"test-item\", \"value\": 100 }";

        Response response = given()
                .spec(requestSpec)
                .body(requestBody)
            .when()
                .post("/api/resource")
            .then()
                .extract().response();

        ReportManager.getTest().info("Request Body: " + requestBody);
        ReportManager.getTest().info("HTTP Status: " + response.getStatusCode());

        Assert.assertEquals(response.getStatusCode(), 201,
            "Expected HTTP 201 Created but got: " + response.getStatusCode());

        ReportManager.getTest().pass("POST /api/resource returned HTTP 201 ✔");
    }
    */

}
