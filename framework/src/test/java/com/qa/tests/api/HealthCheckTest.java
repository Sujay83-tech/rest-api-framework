package com.qa.tests.api;

import com.qa.framework.config.ConfigManager;
import com.qa.framework.utils.ReportManager;
import com.qa.tests.base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * HealthCheckTest — confirms the Render service is alive
 * and the Swagger/OpenAPI endpoints are reachable.
 *
 * These are your FIRST tests — they run before any functional tests.
 * If these fail, the service is down and all other tests can be skipped.
 *
 * Portfolio note: Health check tests are a standard QA practice
 * in BFSI projects — you will find them in every banking test suite.
 */
public class HealthCheckTest extends BaseTest {

    // ── Test 1: Swagger UI page loads ─────────────────────────────────────
    @Test(priority = 1,
          description = "Verify Swagger UI page returns HTTP 200")
    public void swaggerUiShouldBeReachable() {
        ReportManager.createTest(
            "Swagger UI Health Check",
            "Verify the Swagger UI page at /swagger-ui/index.html returns HTTP 200"
        );

        Response response = given()
                .spec(requestSpec)
            .when()
                .get("/swagger-ui/index.html")
            .then()
                .extract().response();

        int statusCode = response.getStatusCode();

        ReportManager.getTest().info("GET /swagger-ui/index.html → HTTP " + statusCode);

        Assert.assertEquals(statusCode, 200,
            "Swagger UI should return HTTP 200 but got: " + statusCode);

        ReportManager.getTest().pass("Swagger UI is reachable — HTTP 200 ✔");
        System.out.println("[HealthCheck] Swagger UI: PASS (HTTP 200)");
    }

    // ── Test 2: OpenAPI spec endpoint is reachable ────────────────────────
    @Test(priority = 2,
          description = "Verify /v3/api-docs returns HTTP 200 and valid JSON")
    public void apiDocsShouldReturnJson() {
        ReportManager.createTest(
            "OpenAPI Spec Health Check",
            "Verify /v3/api-docs returns HTTP 200 with a JSON body"
        );

        Response response = given()
                .spec(requestSpec)
            .when()
                .get("/v3/api-docs")
            .then()
                .statusCode(200)
                .extract().response();

        String contentType = response.getContentType();
        String body = response.getBody().asString();

        ReportManager.getTest().info("GET /v3/api-docs → HTTP " + response.getStatusCode());
        ReportManager.getTest().info("Content-Type: " + contentType);

        // Confirm the body is JSON (starts with '{')
        Assert.assertTrue(body.trim().startsWith("{"),
            "API docs response should be JSON but got: " + body.substring(0, Math.min(100, body.length())));

        // Confirm it has an 'openapi' or 'swagger' version field
        Assert.assertTrue(
            body.contains("\"openapi\"") || body.contains("\"swagger\""),
            "Response should contain openapi/swagger version field"
        );

        ReportManager.getTest().pass("/v3/api-docs returns valid OpenAPI JSON ✔");
        System.out.println("[HealthCheck] API Docs: PASS (valid JSON)");
    }

    // ── Test 3: API spec has at least 1 endpoint ──────────────────────────
    @Test(priority = 3,
          description = "Verify the OpenAPI spec contains at least one endpoint path")
    public void apiSpecShouldHaveEndpoints() {
        ReportManager.createTest(
            "Endpoint Discovery Check",
            "Verify the OpenAPI spec has at least one path/endpoint defined"
        );

        Response response = given()
                .spec(requestSpec)
            .when()
                .get("/v3/api-docs")
            .then()
                .statusCode(200)
                .extract().response();

        // Extract the 'paths' object from the JSON spec
        // If paths is not null and not empty, endpoints are registered
        String paths = response.jsonPath().getString("paths");

        ReportManager.getTest().info("Paths object: " + (paths != null ? paths.substring(0, Math.min(200, paths.length())) : "NULL"));

        Assert.assertNotNull(paths,
            "The OpenAPI spec has no 'paths' — no endpoints are registered");

        Assert.assertFalse(paths.equals("{}"),
            "The OpenAPI spec has an empty 'paths' — no endpoints found");

        ReportManager.getTest().pass("At least one endpoint is registered in the spec ✔");
        System.out.println("[HealthCheck] Endpoint Discovery: PASS");
    }

}
