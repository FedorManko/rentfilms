package com.manko.rentfilms.mapper;

import com.manko.rentfilms.api.models.FilmRentRequestDto;
import com.manko.rentfilms.api.models.RentedFilmResponseDto;
import com.manko.rentfilms.entity.RentedFilmEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface RentedFilmMapper {

  @Mapping(target = "returned", ignore = true)
  @Mapping(target = "price", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "film", ignore = true)
  RentedFilmEntity toRentedFilm(FilmRentRequestDto request);

  @Mapping(target = "title", source = "rentedFilmEntity.film.title")
  @Mapping(target = "type", source = "rentedFilmEntity.film.type")
  @Mapping(target = "filmId", source = "rentedFilmEntity.film.id")
  RentedFilmResponseDto toRentedFilmResponseDto(RentedFilmEntity rentedFilmEntity);
}
