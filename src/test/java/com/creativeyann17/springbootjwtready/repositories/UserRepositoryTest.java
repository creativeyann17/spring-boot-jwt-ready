package com.creativeyann17.springbootjwtready.repositories;

import com.creativeyann17.springbootjwtready.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  public void findByUsernameUser() {
    final User user = userRepository.findByUsername("user").orElse(null);
    assertNotNull(user);
  }

  @Test
  public void findByUsernameAdmin() {
    final User user = userRepository.findByUsername("admin").orElse(null);
    assertNotNull(user);
  }

}