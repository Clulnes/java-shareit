package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final User owner = new User(1L, "O", "o@m.com");
    private final User booker = new User(2L, "B", "b@m.com");
    private final Item item = new Item(1L, "Дрель", "Топ", true, owner, null);
    private final Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
            item, booker, Status.WAITING);

    @Test
    void createBooking_Valid_ReturnsDto() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto req = new BookingDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        assertNotNull(bookingService.createBooking(2L, req));
    }

    @Test
    void createBooking_OwnerBooksOwnItem_ThrowsNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        BookingDto req = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L, req));
    }

    @Test
    void approveBooking_NotOwner_ThrowsValidation() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        assertThrows(ValidationException.class, () -> bookingService.approveBooking(2L, 1L, true));
    }

    @Test
    void approveBooking_Valid_ReturnsDto() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingResponseDto res = bookingService.approveBooking(1L, 1L, true);
        assertEquals(Status.APPROVED, res.getStatus());
    }

    @Test
    void getBookingById_NotOwnerOrBooker_ThrowsNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(3L, 1L));
    }

    @Test
    void getAllByBooker_UserNotFound_ThrowsNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> bookingService.getAllByBooker(1L, "ALL"));
    }

    @Test
    void getAllByBooker_ValidState_ReturnsList() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllBookingByBooker(anyLong())).thenReturn(List.of(booking));

        assertEquals(1, bookingService.getAllByBooker(2L, "ALL").size());
    }

    @Test
    void getAllByBooker_UnknownState_ThrowsValidation() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        assertThrows(ValidationException.class, () -> bookingService.getAllByBooker(2L, "UNKNOWN_STATE"));
    }
}