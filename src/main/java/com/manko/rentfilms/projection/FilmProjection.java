package com.manko.rentfilms.projection;

import com.manko.rentfilms.enums.FilmType;
import java.time.Instant;
import java.util.UUID;

public interface FilmProjection {

  UUID getId();

  String getTitle();

  Instant getCreatedAt();

  Integer getQuantity();

  FilmType getType();

}
