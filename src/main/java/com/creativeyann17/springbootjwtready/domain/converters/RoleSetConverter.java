package com.creativeyann17.springbootjwtready.domain.converters;

import com.creativeyann17.springbootjwtready.domain.User;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class RoleSetConverter implements AttributeConverter<Set<User.Role>, String> {

  @Override
  public String convertToDatabaseColumn(Set<User.Role> strings) {
    return StringUtils.collectionToCommaDelimitedString(strings);
  }

  @Override
  public Set<User.Role> convertToEntityAttribute(String s) {
    return StringUtils.commaDelimitedListToSet(s).stream()
        .map(User.Role::valueOf)
        .collect(Collectors.toSet());
  }
}
