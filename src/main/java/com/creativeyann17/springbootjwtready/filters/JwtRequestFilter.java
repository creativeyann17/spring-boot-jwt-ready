package com.creativeyann17.springbootjwtready.filters;

import com.creativeyann17.springbootjwtready.services.MyUserDetailsService;
import com.creativeyann17.springbootjwtready.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtRequestFilter extends OncePerRequestFilter {

  private static final String BEARER = "Bearer ";

  @Autowired
  private MyUserDetailsService userDetailsService;

  @Autowired
  private JwtUtils jwtUtils;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER)) {
      try {
        final String token = authorizationHeader.replaceFirst(BEARER, "");
        final String username = jwtUtils.extractUsername(token);  // failed if expired
        if (StringUtils.hasText(username)) {
          final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
          if (jwtUtils.validateToken(token, userDetails)) { // should never failed
            final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
          }
        }
      } catch (ExpiredJwtException e) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token expired");
      } catch (MalformedJwtException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed JWT token");
      }
    }
    filterChain.doFilter(request, response);
  }
}
