package com.manko.rentfilms.repository;

import com.manko.rentfilms.entity.RentedFilmEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentedFilmRepository extends JpaRepository<RentedFilmEntity, UUID> {

  @Query(value = """
          SELECT *
          FROM rented_films rf
          WHERE rf.user_id = :user_id
            AND rf.id IN (:rented_film_ids)
            AND NOW() >= rf.created_at + (rf.rent_days * INTERVAL '1 day')
            AND rf.returned = false
      """, nativeQuery = true)
  List<RentedFilmEntity> findActiveRentedFilms(
      @Param("user_id") UUID userId,
      @Param("rented_film_ids") List<UUID> rentedFilmIds
  );

}
