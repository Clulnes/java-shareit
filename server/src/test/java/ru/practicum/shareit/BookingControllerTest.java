package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@ContextConfiguration(classes = ShareItServer.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), any())).thenReturn(new BookingResponseDto());
        mvc.perform(patch("/bookings/1?approved=true").header("X-Sharer-User-Id", 1)).andExpect(status().isOk());
    }

    @Test
    void getAllByBooker() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), any())).thenReturn(List.of(new BookingResponseDto()));
        mvc.perform(get("/bookings?state=ALL").header("X-Sharer-User-Id", 1)).andExpect(status().isOk());
    }

    @Test
    void getAllByOwner() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), any())).thenReturn(List.of(new BookingResponseDto()));

        mvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                        .andExpect(status().isOk());
    }

    @Test
    void createBooking_Valid_Returns200() throws Exception {
        BookingDto dto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        when(bookingService.createBooking(anyLong(), any())).thenReturn(new BookingResponseDto());

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    void getBooking_Valid_Returns200() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(new BookingResponseDto());

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                        .andExpect(status().isOk());
    }
}