package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void createBooking_InvalidDates_ThrowsValidationException() {
        BookingDto dto = new BookingDto(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L, dto));
    }

    @Test
    void createBooking_ItemNotAvailable_ThrowsValidationException() {
        User owner = new User(1L, "тест", "o@m.com");
        Item item = new Item(1L, "Дрель", "Топ", false, owner, null);
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        BookingDto dto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L, dto));
    }
}