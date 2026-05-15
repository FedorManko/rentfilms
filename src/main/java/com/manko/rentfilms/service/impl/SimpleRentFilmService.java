package com.manko.rentfilms.service.impl;

import com.manko.rentfilms.api.models.FilmRentRequestDto;
import com.manko.rentfilms.api.models.FilmReturnRequestDto;
import com.manko.rentfilms.api.models.RentedFilmResponseDto;
import com.manko.rentfilms.api.models.RentedFilmsResponseDto;
import com.manko.rentfilms.entity.FilmEntity;
import com.manko.rentfilms.entity.RentedFilmEntity;
import com.manko.rentfilms.exceptions.FilmsNotFoundException;
import com.manko.rentfilms.exceptions.RentedFilmReturnException;
import com.manko.rentfilms.mapper.RentedFilmMapper;
import com.manko.rentfilms.repository.RentedFilmRepository;
import com.manko.rentfilms.service.FilmService;
import com.manko.rentfilms.service.RentFilmService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class SimpleRentFilmService implements RentFilmService {

  private final FilmService filmService;
  private final RentPriceStrategyFactory rentPriceStrategyFactory;
  private final RentedFilmMapper rentedFilmMapper;
  private final RentedFilmRepository rentedFilmRepository;

  @Transactional
  @Override
  public RentedFilmsResponseDto rentFilms(FilmRentRequestDto request) {
    List<UUID> filmsToRent = request.getFilmIds();
    List<FilmEntity> filmEntities = filmService.getFilmsByIds(filmsToRent);

    if (filmEntities.isEmpty() || filmsToRent.size() != filmEntities.size()) {
      throw new FilmsNotFoundException();
    }

    filmService.updateFilmQuantity(filmEntities);

    List<RentedFilmEntity> rentedFilmEntities = filmEntities.stream()
        .map(film -> {
          RentedFilmEntity rentedFilmEntity = rentedFilmMapper.toRentedFilm(request);
          rentedFilmEntity.setFilm(film);
          var priceStrategy = rentPriceStrategyFactory.findFilmRentPriceStrategy(film.getType());
          rentedFilmEntity.setPrice(priceStrategy.calculateRentPrice(request.getRentDays()));
          return rentedFilmEntity;
        })
        .toList();

    rentedFilmRepository.saveAllAndFlush(rentedFilmEntities);

    List<RentedFilmResponseDto> response = rentedFilmEntities.stream()
        .map(rentedFilmMapper::toRentedFilmResponseDto)
        .toList();

    int totalPrice = response.stream()
        .map(RentedFilmResponseDto::getPrice)
        .mapToInt(Integer::intValue)
        .sum();

    return RentedFilmsResponseDto.builder()
        .films(response)
        .totalPrice(totalPrice)
        .build();
  }

  @Transactional
  @Override
  public RentedFilmsResponseDto returnFilms(FilmReturnRequestDto request) {
    List<RentedFilmEntity> activeRentedFilms = rentedFilmRepository.findActiveRentedFilms(
        request.getUserId(), request.getRentedFilmIds());

    if (activeRentedFilms.isEmpty()) {
      throw new RentedFilmReturnException();
    }

    List<UUID> filmIds = activeRentedFilms.stream()
        .map(rentedFilms -> rentedFilms.getFilm().getId())
        .toList();

    filmService.returnFilm(filmIds);

    int additionalPrice = 0;

    for (RentedFilmEntity rentedFilmEntity : activeRentedFilms) {

      FilmRentPriceStrategy priceStrategy =
          rentPriceStrategyFactory.findFilmRentPriceStrategy(rentedFilmEntity.getFilm().getType());

      Instant now = Instant.now();
      Instant createdAt = rentedFilmEntity.getCreatedAt();

      rentedFilmEntity.setReturned(true);

      int days = (int) ChronoUnit.DAYS.between(createdAt, now);

      if (days > rentedFilmEntity.getRentDays()) {

        Integer previousPrice = rentedFilmEntity.getPrice();
        Integer newPrice = priceStrategy.calculateRentPrice(days);

        rentedFilmEntity.setPrice(newPrice);

        additionalPrice += (newPrice - previousPrice);
      }
    }

    List<RentedFilmResponseDto> response = activeRentedFilms.stream()
        .map(rentedFilmMapper::toRentedFilmResponseDto)
        .toList();

    int totalPrice = response.stream()
        .map(RentedFilmResponseDto::getPrice)
        .mapToInt(Integer::intValue)
        .sum();

    return RentedFilmsResponseDto.builder()
        .films(response)
        .additionalPrice(additionalPrice)
        .totalPrice(totalPrice)
        .build();
  }
}
