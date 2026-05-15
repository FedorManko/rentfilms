package com.manko.rentfilms.service.impl;

import com.manko.rentfilms.api.models.FilmRequestDto;
import com.manko.rentfilms.api.models.FilmToRentItemDto;
import com.manko.rentfilms.api.models.FilmToRentResponseDto;
import com.manko.rentfilms.api.models.PagedFilmsToRentResponseDto;
import com.manko.rentfilms.api.models.Pagination;
import com.manko.rentfilms.entity.FilmEntity;
import com.manko.rentfilms.exceptions.FilmNotFoundException;
import com.manko.rentfilms.mapper.FilmMapper;
import com.manko.rentfilms.repository.FilmRepository;
import com.manko.rentfilms.service.FilmService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class SimpleFilmService implements FilmService {

  private static final int DELETED_QUANTITY = 0;
  private static final int STOCK_DECREMENT_PER_RENT = 1;

  private final FilmRepository filmRepository;
  private final FilmMapper filmMapper;


  @Transactional(readOnly = true)
  @Override
  public PagedFilmsToRentResponseDto getAvailableFilms(PageRequest pageable) {
    var filmsPage = filmRepository.findAllAvailableFilmToRent(pageable);

    Page<FilmToRentItemDto> filmToRentItemDtos = filmsPage.map(filmMapper::toFilmToRentDto);

    return PagedFilmsToRentResponseDto.builder()
        .items(filmToRentItemDtos.getContent())
        .pagination(Pagination.builder()
            .hasNext(filmsPage.hasNext())
            .count(filmsPage.getNumberOfElements())
            .pages(filmsPage.getTotalPages())
            .page(filmsPage.getNumber())
            .size(filmsPage.getSize())
            .build())
        .build();
  }


  @Transactional(readOnly = true)
  @Override
  public FilmToRentResponseDto getFilm(UUID entityId) {
    return filmRepository.findByIdAndNotDeleted(entityId)
        .map(filmMapper::toFilmToRentResponseDto)
        .orElseThrow(FilmNotFoundException::new);
  }

  @Transactional
  @Override
  public void deleteFilm(UUID filmId) {
    filmRepository.findByIdAndNotDeleted(filmId)
        .map(film -> {
          film.setDeleted(true);
          film.setQuantity(DELETED_QUANTITY);
          return film;
        })
        .orElseThrow(FilmNotFoundException::new);
  }

  @Transactional
  @Override
  public FilmToRentResponseDto createFilm(FilmRequestDto filmRequestDto) {
    FilmEntity filmToSave = filmMapper.toFilm(filmRequestDto);
    FilmEntity savedFilm = filmRepository.saveAndFlush(filmToSave);
    return filmMapper.toFilmToRentResponseDto(savedFilm);
  }

  @Transactional(readOnly = true)
  @Override
  public List<FilmEntity> getFilmsByIds(List<UUID> filmsToRent) {
    return filmRepository.findAvailableFilms(filmsToRent);
  }

  @Transactional
  @Override
  public void updateFilmQuantity(List<FilmEntity> filmEntities) {
    filmEntities.forEach(film -> {
      film.setQuantity(film.getQuantity() - STOCK_DECREMENT_PER_RENT);
      if (film.getQuantity() == DELETED_QUANTITY) {
        film.setDeleted(true);
      }
    });

    filmRepository.saveAll(filmEntities);
  }

  @Transactional
  @Override
  public void returnFilm(List<UUID> filmIds) {
    List<FilmEntity> returnedFilms = filmRepository.findByIdIn(filmIds)
        .stream()
        .map(film -> {
          Integer filmQuantity = film.getQuantity();

          if (filmQuantity == 0) {
            film.setDeleted(false);
          }

          film.setQuantity(filmQuantity + STOCK_DECREMENT_PER_RENT);
          return film;
        }).toList();

    filmRepository.saveAll(returnedFilms);

  }
}
