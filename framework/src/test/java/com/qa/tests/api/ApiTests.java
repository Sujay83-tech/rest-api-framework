package com.qa.tests.api;

import com.qa.framework.utils.ReportManager;
import com.qa.tests.api.Files.payloads;
import com.qa.tests.base.BaseTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class ApiTests extends BaseTest {

    @Test(priority = 1, 
          dataProvider = "PlaceData",
          description = "POST /indian-cities-controller/addCity — should return HTTP 200")
    public void addCities(String cityName, String districtName, String stateName) {
        ReportManager.createTest(
            "POST /indian-cities-controller/addCity — Status Code",
            "Verify the addCity endpoint returns HTTP 200"
        );

        RestAssured.baseURI = "https://firststudyproject.onrender.com";

        Response res = given()
                .header("Content-Type", "application/json")
                .body(payloads.Addplace(cityName, districtName, stateName))
                .when()
                .post("indian-cities-controller/addCity")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response();

        // Log response info
        ReportManager.getTest().info("HTTP Status: " + res.getStatusCode());
        ReportManager.getTest().info("Response Body: " + res.getBody().asString());

        // Assert the status code
        Assert.assertEquals(res.getStatusCode(), 200, 
            "Expected HTTP 200 but got: " + res.getStatusCode());

        ReportManager.getTest().pass("POST /indian-cities-controller/addCity returned HTTP 200 ✔");
    }

    @DataProvider(name = "PlaceData")
    public static Object[][] getPlace() {
        return new Object[][] {
            {"cityName1", "districtName1", "stateName1"},
            {"cityName2", "districtName2", "stateName2"}
        };
    }

    // ── Test Pattern: GET with response time check ─────────────────────────
    @Test(priority = 2,
          description = "GET /api/test — response time should be under 5 seconds")
    public void getCities() {
        ReportManager.createTest(
            "GET /api/test — Response Time",
            "Verify the test endpoint responds within 5 seconds (BFSI SLA)"
        );

        // Assuming requestSpec is initialized in BaseTest, otherwise define it here
        Response response = given()
                // .spec(requestSpec) // Uncomment if requestSpec is defined
            .when()
                .get("/api/test")
            .then()
                .extract().response();

        long responseTime = response.getTime();

        // Log response time
        ReportManager.getTest().info("Response time: " + responseTime + " ms");

        // Assert response time is under 5 seconds
        Assert.assertTrue(responseTime < 5000,
            "Response time exceeded 5000ms SLA. Actual: " + responseTime + "ms");

        // Pass report
        ReportManager.getTest().pass("Response time: " + responseTime + "ms — within SLA ✔");
    }
}