package com.manko.rentfilms.repository;

import com.manko.rentfilms.entity.FilmEntity;
import com.manko.rentfilms.projection.FilmProjection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FilmRepository extends JpaRepository<FilmEntity, UUID> {

  @Query(value = """
      SELECT
          ftr.id,
          ftr.title,
          ftr.created_at as createdAt,
          ftr.quantity,
          ftr.type
      FROM films_to_rent ftr
      WHERE ftr.quantity > 0
        AND ftr.deleted = false
      """, nativeQuery = true)
  Page<FilmProjection> findAllAvailableFilmToRent(Pageable pageable);


  @Query(value = """
      SELECT
          ftr.*
      FROM films_to_rent ftr
      WHERE ftr.quantity > 0
        AND ftr.deleted = false
        AND ftr.id = :film_id
      """, nativeQuery = true)
  Optional<FilmEntity> findByIdAndNotDeleted(@Param("film_id") UUID filmId);

  @Query(value = """
      SELECT
          ftr.*
      FROM films_to_rent ftr
      WHERE ftr.quantity > 0
        AND ftr.deleted = false
        AND ftr.id IN :film_ids
      """, nativeQuery = true)
  List<FilmEntity> findAvailableFilms(@Param("film_ids") List<UUID> filmIds);

  List<FilmEntity> findByIdIn(@Param("film_ids") List<UUID> filmIds);

}
