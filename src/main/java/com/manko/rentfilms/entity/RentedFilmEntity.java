package com.manko.rentfilms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rented_films")
@Getter
@Setter
public class RentedFilmEntity extends BaseEntity {

  private UUID userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "film_id", referencedColumnName = "id")
  private FilmEntity film;

  private Integer rentDays;

  private boolean returned = false;

  private Integer price;
}
