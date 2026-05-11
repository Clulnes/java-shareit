package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
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
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
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

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Ванька", "ivan@mail.com");
        booker = new User(2L, "Лешка", "alex@mail.com");
        item = new Item(1L, "Дрель", "Топ", true, owner, null);
        booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker,
                Status.WAITING);
    }

    @Test
    void createBooking_Valid_ReturnsDto() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto req = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        assertNotNull(bookingService.createBooking(2L, req));
    }

    @Test
    void approveBooking_Valid_ReturnsDto() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingResponseDto res = bookingService.approveBooking(1L, 1L, true);
        assertEquals(Status.APPROVED, res.getStatus());
    }

    @Test
    void approveBooking_AlreadyApproved_ThrowsValidation() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        assertThrows(ValidationException.class, () -> bookingService.approveBooking(1L, 1L, true));
    }

    @Test
    void getBookingById_Valid_ReturnsDto() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        assertNotNull(bookingService.getBookingById(1L, 1L));
        assertNotNull(bookingService.getBookingById(2L, 1L));
    }

    @Test
    void getAllByBooker_AllStates() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        bookingService.getAllByBooker(2L, "ALL");
        bookingService.getAllByBooker(2L, "CURRENT");
        bookingService.getAllByBooker(2L, "PAST");
        bookingService.getAllByBooker(2L, "FUTURE");
        bookingService.getAllByBooker(2L, "WAITING");
        bookingService.getAllByBooker(2L, "REJECTED");

        assertThrows(ValidationException.class, () -> bookingService.getAllByBooker(2L, "INVALID_STATE"));
    }

    @Test
    void getAllByOwner_AllStates() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        bookingService.getAllByOwner(1L, "ALL");
        bookingService.getAllByOwner(1L, "CURRENT");
        bookingService.getAllByOwner(1L, "PAST");
        bookingService.getAllByOwner(1L, "FUTURE");
        bookingService.getAllByOwner(1L, "WAITING");
        bookingService.getAllByOwner(1L, "REJECTED");

        assertThrows(ValidationException.class, () -> bookingService.getAllByOwner(1L, "INVALID_STATE"));
    }
}