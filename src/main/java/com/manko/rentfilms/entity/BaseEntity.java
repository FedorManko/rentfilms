package com.manko.rentfilms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {

  @Id
  @GeneratedValue
  protected UUID id;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  protected Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  protected Instant updatedAt;
}
