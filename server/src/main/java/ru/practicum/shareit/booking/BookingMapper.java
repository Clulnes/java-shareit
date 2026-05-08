package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingResponseDto;

public class BookingMapper {
    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingResponseDto.BookerDto(booking.getBooker().getId()),
                new BookingResponseDto.ItemDto(booking.getItem().getId(), booking.getItem().getName())
        );
    }
}
