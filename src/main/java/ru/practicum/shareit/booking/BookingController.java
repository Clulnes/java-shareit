package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponseDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                                     @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllByOwner(userId, state);
    }
}
