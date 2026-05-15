package com.manko.rentfilms.service;

import com.manko.rentfilms.api.models.FilmRequestDto;
import com.manko.rentfilms.api.models.FilmToRentResponseDto;
import com.manko.rentfilms.api.models.PagedFilmsToRentResponseDto;
import com.manko.rentfilms.entity.FilmEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;

public interface FilmService {

  PagedFilmsToRentResponseDto getAvailableFilms(PageRequest pageable);

  FilmToRentResponseDto getFilm(UUID entityId);

  void deleteFilm(UUID filmId);

  FilmToRentResponseDto createFilm(FilmRequestDto filmRequestDto);

  List<FilmEntity> getFilmsByIds(List<UUID> filmsToRent);

  void updateFilmQuantity(List<FilmEntity> filmEntities);

  void returnFilm(List<UUID> filmIds);
}
