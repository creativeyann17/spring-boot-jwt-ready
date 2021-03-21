package com.creativeyann17.springbootjwtready.controllers;

import com.creativeyann17.springbootjwtready.models.AuthRequest;
import com.creativeyann17.springbootjwtready.models.AuthResponse;
import com.creativeyann17.springbootjwtready.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostMapping("/auth/signin")
  public ResponseEntity<AuthResponse> signin(@Validated @RequestBody AuthRequest authRequest) {
    UserDetails userDetails = null;

    try {
      userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
    } catch (AuthenticationException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username");
    }
    if (!passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
    }
    final String token = jwtUtils.generateToken(userDetails);
    return ResponseEntity.ok(AuthResponse.builder().accessToken(token).build());
  }
}
