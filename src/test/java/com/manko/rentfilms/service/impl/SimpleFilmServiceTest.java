package com.manko.rentfilms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.manko.rentfilms.api.models.FilmRequestDto;
import com.manko.rentfilms.api.models.FilmToRentItemDto;
import com.manko.rentfilms.api.models.FilmToRentResponseDto;
import com.manko.rentfilms.api.models.PagedFilmsToRentResponseDto;
import com.manko.rentfilms.api.models.Type;
import com.manko.rentfilms.entity.FilmEntity;
import com.manko.rentfilms.enums.FilmType;
import com.manko.rentfilms.exceptions.FilmNotFoundException;
import com.manko.rentfilms.mapper.FilmMapper;
import com.manko.rentfilms.projection.FilmProjection;
import com.manko.rentfilms.repository.FilmRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@MockitoSettings
class SimpleFilmServiceTest {

  @Mock
  private FilmRepository filmRepository;

  @Mock
  private FilmMapper filmMapper;

  @InjectMocks
  private SimpleFilmService filmService;

  private UUID filmId;
  private FilmEntity filmEntity;
  private FilmToRentResponseDto filmToRentResponseDto;

  @BeforeEach
  void setUp() {
    filmId = UUID.randomUUID();
    filmEntity = new FilmEntity();
    filmEntity.setId(filmId);
    filmEntity.setTitle("Test Film");
    filmEntity.setQuantity(10);
    filmEntity.setDeleted(false);
    filmEntity.setType(FilmType.NEW_RELEASE);

    filmToRentResponseDto = FilmToRentResponseDto.builder()
        .id(filmId)
        .title("Test Film")
        .quantity(10)
        .type(Type.NEW_RELEASE)
        .build();
  }

  @Test
  void getAvailableFilms_ShouldReturnPagedResponse() {
    PageRequest pageRequest = PageRequest.of(0, 10);
    FilmProjection projection = mock(FilmProjection.class);

    Page<FilmProjection> filmsPage = new PageImpl<>(List.of(projection), pageRequest, 1);
    when(filmRepository.findAllAvailableFilmToRent(pageRequest)).thenReturn(filmsPage);

    FilmToRentItemDto itemDto = FilmToRentItemDto.builder()
        .id(filmId)
        .title("Test Film")
        .build();
    when(filmMapper.toFilmToRentDto(any())).thenReturn(itemDto);

    PagedFilmsToRentResponseDto response = filmService.getAvailableFilms(pageRequest);

    assertNotNull(response);
    assertEquals(1, response.getItems().size());
    assertEquals(filmId, response.getItems().get(0).getId());
    assertEquals(0, response.getPagination().getPage());
    assertEquals(1, response.getPagination().getPages());
    verify(filmRepository).findAllAvailableFilmToRent(pageRequest);
  }

  @Test
  void getFilm_WhenFilmExists_ShouldReturnFilm() {
    when(filmRepository.findByIdAndNotDeleted(filmId)).thenReturn(Optional.of(filmEntity));
    when(filmMapper.toFilmToRentResponseDto(filmEntity)).thenReturn(filmToRentResponseDto);

    FilmToRentResponseDto result = filmService.getFilm(filmId);

    assertNotNull(result);
    assertEquals(filmId, result.getId());
    verify(filmRepository).findByIdAndNotDeleted(filmId);
  }

  @Test
  void getFilm_WhenFilmDoesNotExist_ShouldThrowException() {
    when(filmRepository.findByIdAndNotDeleted(filmId)).thenReturn(Optional.empty());

    assertThrows(FilmNotFoundException.class, () -> filmService.getFilm(filmId));
  }

  @Test
  void deleteFilm_WhenFilmExists_ShouldMarkAsDeleted() {
    when(filmRepository.findByIdAndNotDeleted(filmId)).thenReturn(Optional.of(filmEntity));

    filmService.deleteFilm(filmId);

    assertTrue(filmEntity.getDeleted());
    assertEquals(0, filmEntity.getQuantity());
    verify(filmRepository).findByIdAndNotDeleted(filmId);
  }

  @Test
  void deleteFilm_WhenFilmDoesNotExist_ShouldThrowException() {
    when(filmRepository.findByIdAndNotDeleted(filmId)).thenReturn(Optional.empty());

    assertThrows(FilmNotFoundException.class, () -> filmService.deleteFilm(filmId));
  }

  @Test
  void createFilm_ShouldReturnCreatedFilm() {
    FilmRequestDto requestDto = FilmRequestDto.builder()
        .title("New Film")
        .quantity(5)
        .type(Type.NEW_RELEASE)
        .build();

    FilmEntity filmToSave = new FilmEntity();
    filmToSave.setTitle(requestDto.getTitle());
    filmToSave.setQuantity(requestDto.getQuantity());

    FilmEntity savedFilm = new FilmEntity();
    savedFilm.setId(filmId);
    savedFilm.setTitle(requestDto.getTitle());
    savedFilm.setQuantity(requestDto.getQuantity());
    savedFilm.setType(FilmType.valueOf(requestDto.getType().toString()));

    when(filmMapper.toFilm(requestDto)).thenReturn(filmToSave);
    when(filmRepository.saveAndFlush(filmToSave)).thenReturn(savedFilm);
    when(filmMapper.toFilmToRentResponseDto(savedFilm)).thenReturn(filmToRentResponseDto);

    FilmToRentResponseDto result = filmService.createFilm(requestDto);

    assertNotNull(result);
    verify(filmRepository).saveAndFlush(filmToSave);
  }

  @Test
  void getFilmsByIds_ShouldReturnFilms() {
    List<UUID> ids = List.of(filmId);
    List<FilmEntity> films = List.of(filmEntity);
    when(filmRepository.findAvailableFilms(ids)).thenReturn(films);

    List<FilmEntity> result = filmService.getFilmsByIds(ids);

    assertEquals(films, result);
    verify(filmRepository).findAvailableFilms(ids);
  }

  @Test
  void updateFilmQuantity_ShouldDecreaseQuantity() {
    int initialQuantify = 10;
    filmEntity.setQuantity(initialQuantify);
    List<FilmEntity> films = List.of(filmEntity);

    filmService.updateFilmQuantity(films);

    assertEquals(initialQuantify - 1, filmEntity.getQuantity());
    verify(filmRepository).saveAll(films);
  }

  @Test
  void updateFilmQuantity_WhenQuantityBecomesZero_ShouldMarkAsDeleted() {
    filmEntity.setQuantity(1);
    List<FilmEntity> films = List.of(filmEntity);

    filmService.updateFilmQuantity(films);

    assertEquals(0, filmEntity.getQuantity());
    assertTrue(filmEntity.getDeleted());
    verify(filmRepository).saveAll(films);
  }
}
