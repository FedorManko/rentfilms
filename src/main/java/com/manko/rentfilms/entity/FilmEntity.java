package com.manko.rentfilms.entity;

import com.manko.rentfilms.enums.FilmType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "films_to_rent")
@Getter
@Setter
public class FilmEntity extends BaseEntity {

  private String title;

  private Integer quantity;

  @Version
  private Integer version;

  private Boolean deleted = false;

  @Enumerated(EnumType.STRING)
  private FilmType type;

}
