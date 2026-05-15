package com.manko.rentfilms.controller;

import com.manko.rentfilms.api.controller.FilmApi;
import com.manko.rentfilms.api.models.FilmListSort;
import com.manko.rentfilms.api.models.FilmRequestDto;
import com.manko.rentfilms.api.models.FilmToRentResponseDto;
import com.manko.rentfilms.api.models.PagedFilmsToRentResponseDto;
import com.manko.rentfilms.api.models.SortDirection;
import com.manko.rentfilms.service.FilmService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FilmController implements FilmApi {

  private final FilmService filmService;

  @Override
  public PagedFilmsToRentResponseDto getAvailableFilms(
      Integer page,
      Integer size,
      SortDirection direction,
      FilmListSort sort) {
    var sortBy = sort.getValue();
    var directionBy = Direction.fromString(direction.getValue());
    return filmService.getAvailableFilms(PageRequest.of(page, size, directionBy, sortBy));
  }

  @Override
  public FilmToRentResponseDto getFilm(UUID entityId) {
    return filmService.getFilm(entityId);
  }

  @Override
  public FilmToRentResponseDto createFilm(FilmRequestDto filmRequestDto) {
    return filmService.createFilm(filmRequestDto);
  }

  @Override
  public void deleteFilm(UUID filmId) {
    filmService.deleteFilm(filmId);
  }
}
