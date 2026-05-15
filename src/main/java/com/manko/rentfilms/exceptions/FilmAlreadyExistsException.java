package com.manko.rentfilms.exceptions;

import static org.springframework.http.HttpStatus.CONFLICT;

public class FilmAlreadyExistsException extends ApplicationException {

  private static final String FILM_ALREADY_EXISTS =
      "error.validation.film.filmAlreadyExists.message";

  public FilmAlreadyExistsException() {
    super(FILM_ALREADY_EXISTS, CONFLICT);
  }
}
