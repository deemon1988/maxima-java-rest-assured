package org.example;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CatsApiTestsIT {
    /*
     200 = admin token
     401 = no token
     403 = user token
    */

    private RequestSpecification authSpecification;
    private RequestSpecification resourceSpecification;
    private String adminToken;
    private String userToken;

    @BeforeAll
    public void init() {
        authSpecification = new RequestSpecBuilder()
                .setBaseUri(TestsConfig.baseURI)
                .setBasePath(TestsConfig.authPath)
                .setPort(TestsConfig.authPort)
                .build();
        resourceSpecification = new RequestSpecBuilder()
                .setBaseUri(TestsConfig.baseURI)
                .setBasePath(TestsConfig.resourcePath)
                .setPort(TestsConfig.resourcePort)
                .build();
        userToken = given()
                .spec(authSpecification)
                .auth().preemptive()
                .basic(TestsConfig.userName, TestsConfig.userPassword)
             .when().post()
             .then().extract().path("token");

        adminToken = given()
                .spec(authSpecification)
                .auth().preemptive()
                .basic(TestsConfig.adminName, TestsConfig.adminPassword)
              .when().post()
              .then().extract().path("token");
    }

    @Test
    @DisplayName("Resource <host>:8080/cats returns 401 without token")
    public void authWithoutTokenReturns401() {
        given(resourceSpecification)
                .log().all()
             .when().get()
             .then().statusCode(401);
    }

    @Test
    @DisplayName("Resource <host>:8080/cats returns 403 with user token")
    public void authWithUserTokenReturns403() {
        given(resourceSpecification)
                .log().all()
                .header("Authorization", "Bearer " + userToken)
             .when().get()
             .then().statusCode(403);
    }

    @Test
    @DisplayName("Resource <host>:8080/cats returns 200 with admin token")
    public void authWithAdminTokenReturns200() {
        given(resourceSpecification)
                .log().all()
                .header("Authorization", "Bearer " + adminToken)
                .when().get()
                .then()
                .statusCode(200)
                .time(lessThan(500L), TimeUnit.MILLISECONDS);
    }
}
