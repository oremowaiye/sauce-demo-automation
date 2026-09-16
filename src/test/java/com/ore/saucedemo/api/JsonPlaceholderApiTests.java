package com.ore.saucedemo.api;

import com.ore.saucedemo.config.ConfigReader;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * API-layer checks written with RestAssured against a public sandbox API.
 *
 * <p>SauceDemo has no public API of its own, so these run against JSONPlaceholder. The
 * point is the shape of the tests - shared request specification, status and schema
 * assertions, negative cases and a response-time budget - which is the same shape used
 * against a real service.
 */
@Epic("API")
@Feature("JSONPlaceholder REST endpoints")
public class JsonPlaceholderApiTests {

    private RequestSpecification spec;

    @BeforeClass(alwaysRun = true)
    public void setUpSpec() {
        RestAssured.baseURI = ConfigReader.apiBaseUrl();
        spec = new RequestSpecBuilder()
                .setBaseUri(ConfigReader.apiBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                // Log only failures to keep the console readable; attach every
                // request and response to the Allure report for debugging.
                .addFilter(new ErrorLoggingFilter())
                .addFilter(new AllureRestAssured())
                .build();
    }

    @Test(groups = {"smoke", "api"})
    @Story("Reading a resource")
    @Severity(SeverityLevel.CRITICAL)
    @Description("A known post is returned with the expected fields and identifier.")
    public void singlePostIsReturnedWithTheExpectedShape() {
        given()
                .spec(spec)
        .when()
                .get("/posts/{id}", 1)
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("userId", notNullValue())
                .body("title", not(equalTo("")))
                .body("body", notNullValue());
    }

    @Test(groups = {"regression", "api"})
    @Story("Reading a collection")
    @Severity(SeverityLevel.NORMAL)
    @Description("The users collection is non-empty and every entry carries an email address.")
    public void usersCollectionIsWellFormed() {
        Response response = given().spec(spec).when().get("/users").andReturn();

        assertEquals(response.statusCode(), 200, "Unexpected status for GET /users");

        List<Map<String, Object>> users = response.jsonPath().getList("$");

        assertTrue(users.size() > 0, "The users collection was empty");
        assertTrue(users.stream().allMatch(user -> user.get("email") != null),
                "At least one user was returned without an email address");
        assertTrue(users.stream().allMatch(user -> user.get("id") != null),
                "At least one user was returned without an id");
    }

    @Test(groups = {"regression", "api"})
    @Story("Creating a resource")
    @Severity(SeverityLevel.CRITICAL)
    @Description("A POST echoes the submitted body back with a new identifier and 201 Created.")
    public void postCreatesAResource() {
        Map<String, Object> payload = Map.of(
                "title", "Automated regression run",
                "body", "Created by the SauceDemo framework's API suite",
                "userId", 1);

        given()
                .spec(spec)
                .body(payload)
        .when()
                .post("/posts")
        .then()
                .statusCode(201)
                .body("title", equalTo(payload.get("title")))
                .body("body", equalTo(payload.get("body")))
                .body("id", notNullValue());
    }

    @Test(groups = {"regression", "api"})
    @Story("Filtering a collection")
    @Severity(SeverityLevel.NORMAL)
    @Description("A query parameter filters the collection and every row matches the filter.")
    public void commentsCanBeFilteredByPost() {
        List<Integer> postIds = given()
                .spec(spec)
                .queryParam("postId", 1)
        .when()
                .get("/comments")
        .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("postId", Integer.class);

        assertTrue(postIds.size() > 0, "No comments were returned for postId=1");
        assertTrue(postIds.stream().allMatch(id -> id == 1),
                "The filter leaked comments belonging to other posts: " + postIds);
    }

    @Test(groups = {"regression", "api"})
    @Story("Error handling")
    @Severity(SeverityLevel.NORMAL)
    @Description("A request for a resource that does not exist is answered with 404, not 200.")
    public void missingResourceReturnsNotFound() {
        given()
                .spec(spec)
        .when()
                .get("/posts/{id}", 999999)
        .then()
                .statusCode(404);
    }

    @Test(groups = {"regression", "api"})
    @Story("Performance budget")
    @Severity(SeverityLevel.MINOR)
    @Description("A read stays inside a coarse response-time budget - a cheap early warning "
            + "that something has slowed down.")
    public void readStaysWithinTheResponseTimeBudget() {
        long milliseconds = given()
                .spec(spec)
        .when()
                .get("/posts/1")
        .then()
                .statusCode(200)
                .extract()
                .time();

        assertTrue(milliseconds < 5000,
                "GET /posts/1 took " + milliseconds + " ms, which is over the 5000 ms budget");
        System.out.printf("  GET /posts/1 responded in %d ms%n", milliseconds);
    }

    @Test(groups = {"regression", "api"})
    @Story("Reading a collection")
    @Severity(SeverityLevel.MINOR)
    @Description("Nested resources are reachable and belong to their parent.")
    public void postsForAUserAllBelongToThatUser() {
        List<Integer> userIds = given()
                .spec(spec)
                .queryParam("userId", 2)
        .when()
                .get("/posts")
        .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .extract()
                .jsonPath()
                .getList("userId", Integer.class);

        assertTrue(userIds.stream().allMatch(id -> id == 2),
                "Posts were returned for the wrong user: " + userIds);
    }
}
