package com.creativeyann17.springbootjwtready.properties;

import com.creativeyann17.springbootjwtready.filters.JwtRequestFilter;
import com.creativeyann17.springbootjwtready.utils.JwtUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "jwt", ignoreUnknownFields = false)
public class JwtProperties {

  @NotBlank
  private String secret;

  @Min(60)
  @Max(3600)
  private Integer expiration;

  @Bean
  public JwtUtils jwtUtils() {
    return new JwtUtils();
  }

  @Bean
  public JwtRequestFilter jwtRequestFilter() {
    return new JwtRequestFilter();
  }

}
