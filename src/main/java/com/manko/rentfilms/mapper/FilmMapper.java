package com.manko.rentfilms.mapper;

import com.manko.rentfilms.api.models.FilmRequestDto;
import com.manko.rentfilms.api.models.FilmToRentItemDto;
import com.manko.rentfilms.api.models.FilmToRentResponseDto;
import com.manko.rentfilms.entity.FilmEntity;
import com.manko.rentfilms.projection.FilmProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface FilmMapper {

  FilmToRentItemDto toFilmToRentDto(FilmProjection filmProjection);

  FilmToRentResponseDto toFilmToRentResponseDto(FilmEntity filmEntity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  FilmEntity toFilm(FilmRequestDto filmRequestDto);
}
