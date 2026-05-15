package com.manko.rentfilms.exceptions;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class FilmNotFoundException extends ApplicationException {

  private static final String FILM_NOT_FOUND = "error.validation.film.filmDoesNotExist.message";

  public FilmNotFoundException() {
    super(FILM_NOT_FOUND, NOT_FOUND);
  }
}
