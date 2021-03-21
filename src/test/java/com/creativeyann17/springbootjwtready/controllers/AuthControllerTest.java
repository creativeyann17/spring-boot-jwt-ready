package com.creativeyann17.springbootjwtready.controllers;

import com.creativeyann17.springbootjwtready.models.AuthRequest;
import com.creativeyann17.springbootjwtready.models.AuthResponse;
import com.creativeyann17.springbootjwtready.models.ErrorResponse;
import com.creativeyann17.springbootjwtready.properties.JwtProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private JwtProperties jwtProperties;

  @DisplayName("/auth/signin 200")
  @Test
  public void signin() {

    final AuthRequest request = AuthRequest.builder().username("user").password("password").build();
    ResponseEntity<AuthResponse> response =
        restTemplate.postForEntity(prepareUrl("/auth/signin"), request, AuthResponse.class);
    final AuthResponse body = response.getBody();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(StringUtils.hasText(body.getAccessToken()));
  }

  @DisplayName("/auth/signin 400 - bad username")
  @Test
  public void badUsername() {

    final AuthRequest request = AuthRequest.builder().username("foo").password("password").build();
    ResponseEntity<ErrorResponse> response =
        restTemplate.postForEntity(prepareUrl("/auth/signin"), request, ErrorResponse.class);
    final ErrorResponse body = response.getBody();

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid username", body.getMessage());
    assertTrue(StringUtils.hasText(body.getError()));
  }

  @DisplayName("/auth/signin 400 - bad password")
  @Test
  public void badPassword() {

    final AuthRequest request = AuthRequest.builder().username("user").password("foo").build();
    ResponseEntity<ErrorResponse> response =
        restTemplate.postForEntity(prepareUrl("/auth/signin"), request, ErrorResponse.class);
    final ErrorResponse body = response.getBody();

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid password", body.getMessage());
    assertTrue(StringUtils.hasText(body.getError()));
  }


  @DisplayName("/actuator/health - no token - 403")
  @Test
  public void actuatorNoToken() {
    ResponseEntity<String> response = restTemplate.exchange(
        prepareUrl("/actuator/health"), HttpMethod.GET, new HttpEntity(new HttpHeaders()), String.class);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @DisplayName("/actuator/health - malformed token - 403")
  @Test
  public void actuatorMalformedToken() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + "a.b.c");

    ResponseEntity<ErrorResponse> response = restTemplate.exchange(
        prepareUrl("/actuator/health"), HttpMethod.GET, new HttpEntity(headers), ErrorResponse.class);

    final ErrorResponse body = response.getBody();

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Malformed JWT token", body.getMessage());
    assertTrue(StringUtils.hasText(body.getError()));
  }

  @DisplayName("/actuator/health - user token - 403")
  @Test
  public void actuatorWithUserToken() {

    final AuthRequest tokenRequest = AuthRequest.builder().username("user").password("password").build();
    final String token = restTemplate.postForEntity(prepareUrl("/auth/signin"), tokenRequest,
        AuthResponse.class).getBody().getAccessToken();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);

    ResponseEntity<ErrorResponse> response = restTemplate.exchange(
        prepareUrl("/actuator/health"), HttpMethod.GET, new HttpEntity(headers), ErrorResponse.class);

    final ErrorResponse body = response.getBody();

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    //assertTrue(StringUtils.hasText(body.getMessage()));
    assertTrue(StringUtils.hasText(body.getError()));
  }

  @DisplayName("/actuator/health  - admin token - 200")
  @Test
  public void actuatorWithAdminToken() {
    final AuthRequest tokenRequest = AuthRequest.builder().username("admin").password("password").build();
    final String token = restTemplate.postForEntity(prepareUrl("/auth/signin"), tokenRequest,
        AuthResponse.class).getBody().getAccessToken();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);

    ResponseEntity<String> response = restTemplate.exchange(
        prepareUrl("/actuator/health"), HttpMethod.GET, new HttpEntity(headers), String.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @DisplayName("/actuator/health  - admin token expired - 403")
  @Test
  public void actuatorWithAdminTokenExpired() {

    jwtProperties.setExpiration(1);

    final AuthRequest tokenRequest = AuthRequest.builder().username("admin").password("password").build();
    final String token = restTemplate.postForEntity(prepareUrl("/auth/signin"), tokenRequest,
        AuthResponse.class).getBody().getAccessToken();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);

    try {
      Thread.sleep(1100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    ResponseEntity<ErrorResponse> response = restTemplate.exchange(
        prepareUrl("/actuator/health"), HttpMethod.GET, new HttpEntity(headers), ErrorResponse.class);

    final ErrorResponse body = response.getBody();

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertEquals("Token expired", body.getMessage());
    assertTrue(StringUtils.hasText(body.getError()));

    jwtProperties.setExpiration(300);
  }

  @DisplayName("/unknown - user token - 404")
  @Test
  public void notFound() {

    final AuthRequest tokenRequest = AuthRequest.builder().username("user").password("password").build();
    final String token = restTemplate.postForEntity(prepareUrl("/auth/signin"), tokenRequest,
        AuthResponse.class).getBody().getAccessToken();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);

    ResponseEntity<ErrorResponse> response = restTemplate.exchange(
        prepareUrl("/unkown"), HttpMethod.GET, new HttpEntity(headers), ErrorResponse.class);

    final ErrorResponse body = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    //assertTrue(StringUtils.hasText(body.getMessage()));
    assertTrue(StringUtils.hasText(body.getError()));
  }

  private String prepareUrl(String path) {
    return "http://localhost:" + port + path;
  }

}