package com.manko.rentfilms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.manko.rentfilms.enums.FilmType;
import com.manko.rentfilms.enums.PriceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OldFilmRentPriceStrategyTest {

  private OldFilmRentPriceStrategy strategy;

  @BeforeEach
  void setUp() {
    strategy = new OldFilmRentPriceStrategy();
  }

  @Test
  void calculateRentPrice_WhenDaysWithinFlatCount_ShouldReturnBasePrice() {
    int days = 5;
    int expectedPrice = PriceType.BASIC.getPrice();
    assertEquals(expectedPrice, strategy.calculateRentPrice(days));
  }

  @Test
  void calculateRentPrice_WhenDaysExceedFlatCount_ShouldReturnIncreasedPrice() {
    int days = 7;
    int basePrice = PriceType.BASIC.getPrice();
    int expectedPrice = basePrice + basePrice * (7 - 5);
    assertEquals(expectedPrice, strategy.calculateRentPrice(days));
  }

  @Test
  void getFilmType_ShouldReturnOld() {
    assertEquals(FilmType.OLD, strategy.getFilmType());
  }

  @Test
  void getPriceType_ShouldReturnBasic() {
    assertEquals(PriceType.BASIC, strategy.getPriceType());
  }
}
