package com.manko.rentfilms.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manko.rentfilms.api.models.FilmRequestDto;
import com.manko.rentfilms.api.models.FilmToRentItemDto;
import com.manko.rentfilms.api.models.FilmToRentResponseDto;
import com.manko.rentfilms.api.models.PagedFilmsToRentResponseDto;
import com.manko.rentfilms.api.models.Pagination;
import com.manko.rentfilms.api.models.Type;
import com.manko.rentfilms.service.FilmService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private FilmService filmService;

  @Autowired
  private ObjectMapper objectMapper;

  private UUID filmId;
  private FilmToRentResponseDto filmResponseDto;

  @BeforeEach
  void setUp() {
    filmId = UUID.randomUUID();
    filmResponseDto = FilmToRentResponseDto.builder()
        .id(filmId)
        .title("Test Film")
        .quantity(10)
        .type(Type.NEW_RELEASE)
        .build();
  }

  @Test
  void getAvailableFilms_ShouldReturnPagedResponse() throws Exception {
    PagedFilmsToRentResponseDto pagedResponse = PagedFilmsToRentResponseDto.builder()
        .items(List.of(FilmToRentItemDto.builder()
            .id(filmId)
            .title("Test Film")
            .type(Type.NEW_RELEASE)
            .build()))
        .pagination(Pagination.builder()
            .page(0)
            .size(10)
            .pages(1)
            .count(1)
            .hasNext(false)
            .build())
        .build();

    when(filmService.getAvailableFilms(any(PageRequest.class))).thenReturn(pagedResponse);

    mockMvc.perform(get("/v1/films-to-rent")
            .param("page", "0")
            .param("size", "10")
            .param("direction", "asc")
            .param("sort", "title"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[0].id").value(filmId.toString()))
        .andExpect(jsonPath("$.items[0].title").value("Test Film"))
        .andExpect(jsonPath("$.items[0].type").value("NEW_RELEASE"))
        .andExpect(jsonPath("$.pagination.page").value(0));
  }

  @Test
  void getFilm_WhenFilmExists_ShouldReturnFilm() throws Exception {
    when(filmService.getFilm(filmId)).thenReturn(filmResponseDto);

    mockMvc.perform(get("/v1/films-to-rent/{filmId}", filmId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(filmId.toString()))
        .andExpect(jsonPath("$.title").value("Test Film"));
  }

  @Test
  void createFilm_ShouldReturnCreatedFilm() throws Exception {
    FilmRequestDto requestDto = FilmRequestDto.builder()
        .title("New Film")
        .type(Type.NEW_RELEASE)
        .quantity(5)
        .build();

    when(filmService.createFilm(any(FilmRequestDto.class))).thenReturn(filmResponseDto);

    mockMvc.perform(post("/v1/films-to-rent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(filmId.toString()))
        .andExpect(jsonPath("$.title").value("Test Film"))
        .andExpect(jsonPath("$.type").value("NEW_RELEASE"));
  }

  @Test
  void deleteFilm_WhenFilmExists_ShouldReturnOk() throws Exception {
    mockMvc.perform(delete("/v1/films-to-rent/{filmId}", filmId))
        .andExpect(status().isNoContent());

    verify(filmService).deleteFilm(filmId);
  }
}
