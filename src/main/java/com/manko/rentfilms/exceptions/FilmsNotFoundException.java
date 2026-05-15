package com.manko.rentfilms.exceptions;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class FilmsNotFoundException extends ApplicationException {

  private static final String FILMS_NOT_FOUND = "error.validation.film.filmsDoesNotExist.message";

  public FilmsNotFoundException() {
    super(FILMS_NOT_FOUND, NOT_FOUND);
  }
}
