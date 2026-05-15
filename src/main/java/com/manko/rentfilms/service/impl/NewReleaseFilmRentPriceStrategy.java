package com.manko.rentfilms.service.impl;

import com.manko.rentfilms.enums.FilmType;
import com.manko.rentfilms.enums.PriceType;
import org.springframework.stereotype.Component;

@Component
class NewReleaseFilmRentPriceStrategy implements FilmRentPriceStrategy {

  @Override
  public Integer calculateRentPrice(int daysRented) {
    return getPriceType().getPrice() * daysRented;
  }

  @Override
  public FilmType getFilmType() {
    return FilmType.NEW_RELEASE;
  }

  @Override
  public PriceType getPriceType() {
    return PriceType.PREMIUM;
  }
}
