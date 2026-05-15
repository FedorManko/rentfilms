package com.manko.rentfilms.exceptions;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UserRentSameFilmException extends ApplicationException {

  private static final String USER_RENT_SAME_FILM =
      "error.validation.film.userRentSameFilmException.message";

  public UserRentSameFilmException() {
    super(USER_RENT_SAME_FILM, BAD_REQUEST);
  }
}
