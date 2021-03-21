package com.creativeyann17.springbootjwtready.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class MyUtils {
  public static <T> Collection<T> emptyIfNull(Collection<T> c) {
    return Optional.ofNullable(c).orElse(Collections.emptyList());
  }
}
