package com.creativeyann17.springbootjwtready.services;

import com.creativeyann17.springbootjwtready.repositories.UserRepository;
import com.creativeyann17.springbootjwtready.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyUserDetailsService implements UserDetailsService {

  private static final String ROLE_PREFIX = "ROLE_";

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUsername(username)
        .map(user -> new User(user.getUsername(), user.getPassword(), mapRolesToGrantedAuthorities(user.getRoles())))
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }

  private List<GrantedAuthority> mapRolesToGrantedAuthorities(Collection<com.creativeyann17.springbootjwtready.domain.User.Role> roles) {
    return MyUtils.emptyIfNull(roles).stream()
        .map(role -> new SimpleGrantedAuthority(normalizeRoleName(role)))
        .collect(Collectors.toList());
  }

  // Spring boot security implicitly add ROLE_ prefix
  private String normalizeRoleName(com.creativeyann17.springbootjwtready.domain.User.Role role) {
    return role.name().startsWith(ROLE_PREFIX) ? role.name() : ROLE_PREFIX + role.name();
  }
}
