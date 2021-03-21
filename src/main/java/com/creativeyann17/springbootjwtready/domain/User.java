package com.creativeyann17.springbootjwtready.domain;

import com.creativeyann17.springbootjwtready.domain.converters.RoleSetConverter;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(
    name = "users",
    indexes = {@Index(name = "username_index", columnList = "username", unique = true)})
@Data
public class User {

  public enum Role {
    USER,
    ADMIN
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "roles", nullable = false)
  @Convert(converter = RoleSetConverter.class)
  private Set<Role> roles;
}
