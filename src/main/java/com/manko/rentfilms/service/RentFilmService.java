package com.manko.rentfilms.service;

import com.manko.rentfilms.api.models.FilmRentRequestDto;
import com.manko.rentfilms.api.models.FilmReturnRequestDto;
import com.manko.rentfilms.api.models.RentedFilmsResponseDto;

public interface RentFilmService {

  RentedFilmsResponseDto rentFilms(FilmRentRequestDto request);

  RentedFilmsResponseDto returnFilms(FilmReturnRequestDto request);
}
