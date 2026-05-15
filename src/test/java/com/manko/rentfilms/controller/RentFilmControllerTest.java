package com.manko.rentfilms.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manko.rentfilms.api.models.FilmRentRequestDto;
import com.manko.rentfilms.api.models.FilmReturnRequestDto;
import com.manko.rentfilms.api.models.RentedFilmResponseDto;
import com.manko.rentfilms.api.models.RentedFilmsResponseDto;
import com.manko.rentfilms.service.RentFilmService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RentFilmController.class)
class RentFilmControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private RentFilmService rentFilmService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void rentFilms_ShouldReturnRentedFilms() throws Exception {
    UUID filmId = UUID.randomUUID();
    FilmRentRequestDto requestDto = FilmRentRequestDto.builder()
        .userId(UUID.randomUUID())
        .filmIds(List.of(filmId))
        .rentDays(3)
        .build();

    RentedFilmsResponseDto responseDto = new RentedFilmsResponseDto(
        List.of(RentedFilmResponseDto.builder()
            .filmId(filmId)
            .price(30)
            .build()),
        0,
        30
    );

    when(rentFilmService.rentFilms(any(FilmRentRequestDto.class))).thenReturn(responseDto);

    mockMvc.perform(post("/v1/rented-films/rent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.films[0].filmId").value(filmId.toString()))
        .andExpect(jsonPath("$.films[0].price").value(30));
  }

  @Test
  void returnFilms_ShouldReturnRentedFilms() throws Exception {
    UUID filmId = UUID.randomUUID();
    FilmReturnRequestDto requestDto = FilmReturnRequestDto.builder()
        .userId(UUID.randomUUID())
        .rentedFilmIds(List.of(filmId))
        .build();

    RentedFilmsResponseDto responseDto = new RentedFilmsResponseDto(
        List.of(RentedFilmResponseDto.builder()
            .filmId(filmId)
            .price(30)
            .build()),
        0,
        30
    );

    when(rentFilmService.returnFilms(any(FilmReturnRequestDto.class))).thenReturn(responseDto);

    mockMvc.perform(patch("/v1/rented-films/return")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.films[0].filmId").value(filmId.toString()))
        .andExpect(jsonPath("$.additionalPrice").value(0));
  }
}
