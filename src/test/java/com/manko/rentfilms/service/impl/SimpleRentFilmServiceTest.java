package com.manko.rentfilms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.manko.rentfilms.api.models.FilmRentRequestDto;
import com.manko.rentfilms.api.models.FilmReturnRequestDto;
import com.manko.rentfilms.api.models.RentedFilmResponseDto;
import com.manko.rentfilms.api.models.RentedFilmsResponseDto;
import com.manko.rentfilms.entity.FilmEntity;
import com.manko.rentfilms.entity.RentedFilmEntity;
import com.manko.rentfilms.enums.FilmType;
import com.manko.rentfilms.exceptions.FilmsNotFoundException;
import com.manko.rentfilms.exceptions.RentedFilmReturnException;
import com.manko.rentfilms.mapper.RentedFilmMapper;
import com.manko.rentfilms.repository.RentedFilmRepository;
import com.manko.rentfilms.service.FilmService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class SimpleRentFilmServiceTest {

  @Mock
  private FilmService filmService;

  @Mock
  private RentPriceStrategyFactory rentPriceStrategyFactory;

  @Mock
  private RentedFilmMapper rentedFilmMapper;

  @Mock
  private RentedFilmRepository rentedFilmRepository;

  @InjectMocks
  private SimpleRentFilmService rentFilmService;

  private UUID userId;
  private UUID filmId;
  private FilmEntity filmEntity;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    filmId = UUID.randomUUID();

    filmEntity = new FilmEntity();
    filmEntity.setId(filmId);
    filmEntity.setTitle("Matrix");
    filmEntity.setType(FilmType.OLD);
    filmEntity.setQuantity(5);
  }

  @Test
  void rentFilms_ShouldReturnRentedFilms() {
    FilmRentRequestDto request = FilmRentRequestDto.builder()
        .userId(userId)
        .filmIds(List.of(filmId))
        .rentDays(3)
        .build();

    when(filmService.getFilmsByIds(List.of(filmId))).thenReturn(List.of(filmEntity));

    RentedFilmEntity rentedFilmEntity = new RentedFilmEntity();
    rentedFilmEntity.setFilm(filmEntity);
    when(rentedFilmMapper.toRentedFilm(request)).thenReturn(rentedFilmEntity);

    FilmRentPriceStrategy priceStrategy = mock(FilmRentPriceStrategy.class);
    when(rentPriceStrategyFactory.findFilmRentPriceStrategy(FilmType.OLD))
        .thenReturn(priceStrategy);
    when(priceStrategy.calculateRentPrice(3)).thenReturn(30);

    RentedFilmResponseDto responseDto = RentedFilmResponseDto.builder()
        .filmId(filmId)
        .price(30)
        .title("Matrix")
        .build();
    when(rentedFilmMapper.toRentedFilmResponseDto(rentedFilmEntity)).thenReturn(responseDto);

    RentedFilmsResponseDto result = rentFilmService.rentFilms(request);

    assertNotNull(result);
    assertEquals(1, result.getFilms().size());
    assertEquals(30, result.getTotalPrice());
    assertEquals(30, result.getFilms().get(0).getPrice());

    verify(filmService).updateFilmQuantity(any());
    verify(rentedFilmRepository).saveAllAndFlush(any());
  }

  @Test
  void rentFilms_WhenNoFilmsFound_ShouldThrowException() {
    FilmRentRequestDto request = FilmRentRequestDto.builder()
        .userId(userId)
        .filmIds(List.of(filmId))
        .build();

    when(filmService.getFilmsByIds(any())).thenReturn(Collections.emptyList());

    assertThrows(FilmsNotFoundException.class, () -> rentFilmService.rentFilms(request));
  }

  @Test
  void returnFilms_ShouldReturnRentedFilms() {
    FilmReturnRequestDto request = FilmReturnRequestDto.builder()
        .userId(userId)
        .rentedFilmIds(List.of(filmId))
        .build();

    RentedFilmEntity rentedFilmEntity = new RentedFilmEntity();
    rentedFilmEntity.setFilm(filmEntity);
    rentedFilmEntity.setPrice(30);
    rentedFilmEntity.setCreatedAt(Instant.now().plus(1, ChronoUnit.HOURS));
    rentedFilmEntity.setRentDays(3);

    when(rentedFilmRepository.findActiveRentedFilms(userId, List.of(filmId)))
        .thenReturn(List.of(rentedFilmEntity));

    RentedFilmResponseDto responseDto = RentedFilmResponseDto.builder()
        .filmId(filmId)
        .price(30)
        .build();
    when(rentedFilmMapper.toRentedFilmResponseDto(rentedFilmEntity)).thenReturn(responseDto);

    RentedFilmsResponseDto result = rentFilmService.returnFilms(request);

    assertNotNull(result);
    assertTrue(rentedFilmEntity.isReturned());
    assertEquals(0, result.getAdditionalPrice());
  }

  @Test
  void returnFilms_WithDelay_ShouldCalculateAdditionalPrice() {
    FilmReturnRequestDto request = FilmReturnRequestDto.builder()
        .userId(userId)
        .rentedFilmIds(List.of(filmId))
        .build();

    RentedFilmEntity rentedFilmEntity = new RentedFilmEntity();
    rentedFilmEntity.setFilm(filmEntity);
    rentedFilmEntity.setPrice(30);
    rentedFilmEntity.setCreatedAt(Instant.now().minus(5, ChronoUnit.DAYS));
    rentedFilmEntity.setRentDays(5);

    when(rentedFilmRepository.findActiveRentedFilms(userId, List.of(filmId)))
        .thenReturn(List.of(rentedFilmEntity));

    FilmRentPriceStrategy priceStrategy = mock(FilmRentPriceStrategy.class);
    when(rentPriceStrategyFactory
        .findFilmRentPriceStrategy(FilmType.OLD)).thenReturn(priceStrategy);

    RentedFilmResponseDto responseDto = RentedFilmResponseDto.builder()
        .filmId(filmId)
        .price(50)
        .build();
    when(rentedFilmMapper.toRentedFilmResponseDto(rentedFilmEntity)).thenReturn(responseDto);

    RentedFilmsResponseDto result = rentFilmService.returnFilms(request);

    assertNotNull(result);
    assertEquals(30, rentedFilmEntity.getPrice());
    assertTrue(rentedFilmEntity.isReturned());
  }

  @Test
  void returnFilms_WhenNoActiveRentsFound_ShouldThrowException() {
    FilmReturnRequestDto request = FilmReturnRequestDto.builder()
        .userId(userId)
        .rentedFilmIds(List.of(filmId))
        .build();

    when(rentedFilmRepository.findActiveRentedFilms(any(), any()))
        .thenReturn(Collections.emptyList());

    assertThrows(RentedFilmReturnException.class, () -> rentFilmService.returnFilms(request));
  }
}
