package com.manko.rentfilms.controller.advice;

import static com.manko.rentfilms.api.models.RestError.builder;

import com.manko.rentfilms.api.models.RestError;
import com.manko.rentfilms.exceptions.ApplicationException;
import com.manko.rentfilms.exceptions.FilmAlreadyExistsException;
import com.manko.rentfilms.exceptions.UserRentSameFilmException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class CommonExceptionHandler {

  private static final String DEFAULT_ERROR = "error.validation.default.message";
  private static final String FILM_TITLE_VIOLATION = "films_to_rent_title_key";
  private static final String USER_RENT_SAME_FILM_VIOLATION = "uq_rented_film_user_film_active";

  private static final String JAKARTA_PREFIX = "{jakarta.validation.constraints.";
  private static final String JAKARTA_POSTFIX = ".message}";
  private final MessageSource messageSource;

  @ExceptionHandler(exception = ApplicationException.class)
  private ResponseEntity<RestError> handleApplicationException(
      HttpServletRequest request,
      ApplicationException ex
  ) {

    var i18nMessage = messageSource.getMessage(
        ex.getMessage(),
        null,
        Locale.ENGLISH
    );

    var builder = builder()
        .url(request.getRequestURI())
        .method(request.getMethod())
        .message(i18nMessage)
        .timestamp(Instant.now());

    return ResponseEntity
        .status(ex.getHttpStatus())
        .body(builder.build());
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(exception = ConstraintViolationException.class)
  RestError handleConstraintViolationException(
      HttpServletRequest request,
      ConstraintViolationException ex
  ) {

    var builder = builder()
        .url(request.getRequestURI())
        .method(request.getMethod())
        .timestamp(Instant.now());

    var i18n = getI18Message(ex);

    builder.message(i18n);

    return builder.build();
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(exception = MethodArgumentNotValidException.class)
  RestError handleValidationException(
      HttpServletRequest request,
      MethodArgumentNotValidException ex
  ) {

    var i18nMessage = getI18Message(ex);

    var builder = builder()
        .url(request.getRequestURI())
        .method(request.getMethod())
        .message(i18nMessage)
        .timestamp(Instant.now());

    return builder.build();
  }

  @ExceptionHandler(exception = Exception.class)
  protected ResponseEntity<RestError> handleCommonException(
      HttpServletRequest request,
      Exception ex
  ) {

    var builder = builder()
        .url(request.getRequestURI())
        .method(request.getMethod())
        .message(ex.getMessage())
        .timestamp(Instant.now());

    log.error(ex.getMessage(), ex);

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(builder.build());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<RestError> handleDataIntegrityException(
      HttpServletRequest request,
      DataIntegrityViolationException ex
  ) {

    log.error(ex.getMessage(), ex);

    var constraintName = Optional
        .ofNullable(ex.getCause())
        .filter(org.hibernate.exception.ConstraintViolationException.class::isInstance)
        .map(org.hibernate.exception.ConstraintViolationException.class::cast)
        .map(org.hibernate.exception.ConstraintViolationException::getConstraintName)
        .orElse(null);

    if (FILM_TITLE_VIOLATION.equals(constraintName)) {
      return handleApplicationException(request, new FilmAlreadyExistsException());
    }

    if (USER_RENT_SAME_FILM_VIOLATION.equals(constraintName)) {
      return handleApplicationException(request, new UserRentSameFilmException());
    }

    return handleCommonException(request, ex);
  }


  private String getI18Message(ConstraintViolationException ex) {

    String defaultMessage = messageSource.getMessage(
        DEFAULT_ERROR,
        null,
        Locale.ENGLISH
    );

    return ex.getConstraintViolations().stream()
        .map(this::getConstraintPath)
        .limit(1)
        .map(code -> messageSource.getMessage(
            code,
            null,
            null,
            Locale.ENGLISH
        ))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(defaultMessage);
  }

  private String getI18Message(MethodArgumentNotValidException ex) {
    var bindingResult = ex.getBindingResult();

    String defaultMessage = messageSource.getMessage(
        DEFAULT_ERROR,
        null,
        Locale.ENGLISH
    );

    return bindingResult
        .getFieldErrors().stream()
        .map(DefaultMessageSourceResolvable::getCodes)
        .filter(Objects::nonNull)
        .limit(1)
        .map(codes -> codes[0])
        .map(code -> messageSource.getMessage(code, null, null, Locale.ENGLISH))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(defaultMessage);
  }

  private String getConstraintPath(ConstraintViolation<?> violation) {

    var messageTemplate = violation.getMessageTemplate()
        .replace(JAKARTA_PREFIX, "")
        .replace(JAKARTA_POSTFIX, "");
    var propertyPath = violation.getPropertyPath().toString();
    return "%s.%s".formatted(messageTemplate, propertyPath);
  }

}
