package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ventionteams.medfast.config.extension.PostgreContainerExtension;
import com.ventionteams.medfast.config.util.EntityProvider;
import com.ventionteams.medfast.config.util.PatientProvider;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.entity.User;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the authorization controller functionality with integration tests.
 */
@ExtendWith(PostgreContainerExtension.class)
public class AuthControllerTests extends IntegrationTest {

  @Autowired
  private EntityProvider<User> patientProvider;

  @Test
  public void signIn_ValidRequest_CreatesTokenAndReturnsOk() {
    User user = patientProvider.provide();
    SignInRequest request = new SignInRequest(user.getEmail(),
        ((PatientProvider) patientProvider).getRawPassword(user.getEmail()));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/signin")
        .then()
        .statusCode(200)
        .body("data.tokenType", equalTo("Bearer"))
        .body("data.accessToken", notNullValue())
        .body("data.refreshToken", notNullValue())
        .body("data.expiresIn", notNullValue())
        .body("data.refreshExpiresIn", notNullValue());
  }
}
