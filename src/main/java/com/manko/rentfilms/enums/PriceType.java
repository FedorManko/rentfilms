package com.manko.rentfilms.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PriceType {
  PREMIUM(40), BASIC(30);

  private final Integer price;
}
