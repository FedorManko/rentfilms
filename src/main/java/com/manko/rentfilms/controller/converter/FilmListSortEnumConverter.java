package com.manko.rentfilms.controller.converter;

import com.manko.rentfilms.api.models.FilmListSort;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FilmListSortEnumConverter implements Converter<String, FilmListSort> {

  @Override
  public FilmListSort convert(@Nullable String source) {
    return FilmListSort.fromValue(source);
  }
}
