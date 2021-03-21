package com.creativeyann17.springbootjwtready.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
  @NotBlank
  private String username;
  @NotBlank
  private String password;
}
