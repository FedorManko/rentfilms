package com.manko.rentfilms.controller;

import com.manko.rentfilms.api.controller.RentFilmApi;
import com.manko.rentfilms.api.models.FilmRentRequestDto;
import com.manko.rentfilms.api.models.FilmReturnRequestDto;
import com.manko.rentfilms.api.models.RentedFilmsResponseDto;
import com.manko.rentfilms.service.RentFilmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RentFilmController implements RentFilmApi {

  private final RentFilmService rentFilmService;

  @Override
  public RentedFilmsResponseDto rentFilms(FilmRentRequestDto filmRentRequestDto) {
    return rentFilmService.rentFilms(filmRentRequestDto);
  }

  @Override
  public RentedFilmsResponseDto returnFilms(FilmReturnRequestDto filmReturnRequestDto) {
    return rentFilmService.returnFilms(filmReturnRequestDto);
  }
}
