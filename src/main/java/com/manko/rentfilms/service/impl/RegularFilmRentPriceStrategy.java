package com.manko.rentfilms.service.impl;

import com.manko.rentfilms.enums.FilmType;
import com.manko.rentfilms.enums.PriceType;
import org.springframework.stereotype.Component;

@Component
class RegularFilmRentPriceStrategy implements FilmRentPriceStrategy {

  private static final int FLAT_COUNT = 3;

  @Override
  public Integer calculateRentPrice(int daysRented) {
    Integer basePrice = getPriceType().getPrice();

    if (daysRented > FLAT_COUNT) {
      int restDays = daysRented - FLAT_COUNT;
      return basePrice + basePrice * restDays;
    }

    return basePrice;
  }

  @Override
  public FilmType getFilmType() {
    return FilmType.REGULAR;
  }

  @Override
  public PriceType getPriceType() {
    return PriceType.BASIC;
  }
}
