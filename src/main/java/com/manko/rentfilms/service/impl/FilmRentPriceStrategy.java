package com.manko.rentfilms.service.impl;

import com.manko.rentfilms.enums.FilmType;
import com.manko.rentfilms.enums.PriceType;

interface FilmRentPriceStrategy {

  Integer calculateRentPrice(int daysRented);

  FilmType getFilmType();

  PriceType getPriceType();

}
