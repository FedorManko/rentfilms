package com.manko.rentfilms.exceptions;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class RentedFilmReturnException extends ApplicationException {

  private static final String RENTED_FILM_RETURN_EARLY =
      "error.validation.film.rentedFilmReturnEarly.message";

  public RentedFilmReturnException() {
    super(RENTED_FILM_RETURN_EARLY, BAD_REQUEST);
  }
}
