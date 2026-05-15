package com.manko.rentfilms.service.impl;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.manko.rentfilms.enums.FilmType;
import com.manko.rentfilms.exceptions.ApplicationException;
import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class RentPriceStrategyFactory {

  private final List<FilmRentPriceStrategy> strategies;

  private Map<FilmType, FilmRentPriceStrategy> strategyMap;

  @PostConstruct
  void init() {
    strategyMap = strategies.stream()
        .collect(Collectors.toMap(
            FilmRentPriceStrategy::getFilmType,
            Function.identity(),
            (a, b) -> a,
            () -> new EnumMap<>(FilmType.class)
        ));
  }


  public FilmRentPriceStrategy findFilmRentPriceStrategy(FilmType filmType) {
    return strategyMap.values().stream()
        .filter(strategy -> strategy.getFilmType().equals(filmType))
        .findFirst()
        .orElseThrow(() -> new ApplicationException(
            "Incorrect film type rent strategy: " + filmType.name(), BAD_REQUEST) {
        });
  }

}
