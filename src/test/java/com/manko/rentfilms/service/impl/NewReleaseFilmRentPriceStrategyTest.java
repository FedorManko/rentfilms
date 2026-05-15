package com.manko.rentfilms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.manko.rentfilms.enums.FilmType;
import com.manko.rentfilms.enums.PriceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NewReleaseFilmRentPriceStrategyTest {

  private NewReleaseFilmRentPriceStrategy strategy;

  @BeforeEach
  void setUp() {
    strategy = new NewReleaseFilmRentPriceStrategy();
  }

  @Test
  void calculateRentPrice_ShouldReturnCorrectPrice() {
    int days = 3;
    int expectedPrice = PriceType.PREMIUM.getPrice() * days;
    assertEquals(expectedPrice, strategy.calculateRentPrice(days));
  }

  @Test
  void getFilmType_ShouldReturnNewRelease() {
    assertEquals(FilmType.NEW_RELEASE, strategy.getFilmType());
  }

  @Test
  void getPriceType_ShouldReturnPremium() {
    assertEquals(PriceType.PREMIUM, strategy.getPriceType());
  }
}
