package com.creativeyann17.springbootjwtready.utils;

import com.creativeyann17.springbootjwtready.properties.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

  @InjectMocks
  private JwtUtils jwtUtils;

  @Mock
  private JwtProperties jwtProperties;

  @BeforeEach
  public void beforeAll() {
    when(jwtProperties.getSecret()).thenReturn("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    when(jwtProperties.getExpiration()).thenReturn(300);
  }

  @Test
  public void createToken() {
    final User user = new User("foo", "bar", List.of());
    final String token = jwtUtils.generateToken(user);
    assertTrue(StringUtils.hasText(token));
  }

  @Test
  public void validateToken() {
    final User user = new User("foo", "bar", List.of());
    final String token = jwtUtils.generateToken(user);
    assertTrue(jwtUtils.validateToken(token, user));
  }

  @Test
  public void extractUsername() {
    final User user = new User("foo", "bar", List.of());
    final String token = jwtUtils.generateToken(user);
    assertEquals(user.getUsername(), jwtUtils.extractUsername(token));
  }
}