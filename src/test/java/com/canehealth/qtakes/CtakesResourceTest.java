package com.canehealth.qtakes;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;


@QuarkusTest
public class CtakesResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/analyze");
    }

}