package org.example;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.restassured.RestAssured.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthApiTestsIT {
    /*

    post без Basic Auth (correct user-pass) = 401
    post c Basic Auth (correct user-pass) = 200 + token
   // post c Basic Auth (incorrect user-pass) =
    */

    private RequestSpecification authSpecification;
    @BeforeAll
    public void init(){
        authSpecification = new RequestSpecBuilder()
                //.setBaseUri("http://localhost:8081/auth")
                .setBaseUri(TestsConfig.baseURI)
                .setBasePath(TestsConfig.authPath)
                .build();
    }
    @Test
    @DisplayName("Resource <host>:8081/uth returns 401 without Basic Auth")
    public void authWithIncorrectBasicAuthReturns401(){
    given()
            .baseUri("http://localhost:8081/auth")
            .log().all()
    .when().post()
    .then().statusCode(401);
    }

    @Test
    @DisplayName("Resource <host>:8081/uth returns 200 without Basic Auth")
    public void authWithIncorrectBasicAuthReturns200(){
        given()
                .baseUri("http://localhost:8081/auth")
                .auth().preemptive().basic(TestsConfig.adminName,TestsConfig.adminPassword)
                .log().all()
                .when().post()
                .then().statusCode(200);
    }
}
