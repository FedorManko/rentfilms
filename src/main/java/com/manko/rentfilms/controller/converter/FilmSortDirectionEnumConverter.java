package com.manko.rentfilms.controller.converter;

import com.manko.rentfilms.api.models.SortDirection;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class FilmSortDirectionEnumConverter implements Converter<String, SortDirection> {

  @Override
  public SortDirection convert(@Nullable String source) {
    return SortDirection.fromValue(source);
  }
}
