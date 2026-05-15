package com.manko.rentfilms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.manko.rentfilms.enums.FilmType;
import com.manko.rentfilms.enums.PriceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegularFilmRentPriceStrategyTest {

  private RegularFilmRentPriceStrategy strategy;

  @BeforeEach
  void setUp() {
    strategy = new RegularFilmRentPriceStrategy();
  }

  @Test
  void calculateRentPrice_WhenDaysWithinFlatCount_ShouldReturnBasePrice() {
    int days = 3;
    int expectedPrice = PriceType.BASIC.getPrice();
    assertEquals(expectedPrice, strategy.calculateRentPrice(days));
  }

  @Test
  void calculateRentPrice_WhenDaysExceedFlatCount_ShouldReturnIncreasedPrice() {
    int days = 5;
    int basePrice = PriceType.BASIC.getPrice();
    int expectedPrice = basePrice + basePrice * (5 - 3);
    assertEquals(expectedPrice, strategy.calculateRentPrice(days));
  }

  @Test
  void getFilmType_ShouldReturnRegular() {
    assertEquals(FilmType.REGULAR, strategy.getFilmType());
  }

  @Test
  void getPriceType_ShouldReturnBasic() {
    assertEquals(PriceType.BASIC, strategy.getPriceType());
  }
}
