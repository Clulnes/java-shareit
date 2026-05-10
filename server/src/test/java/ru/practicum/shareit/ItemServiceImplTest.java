package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock private ItemRepository itemRepository;
    @Mock private UserRepository userRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private CommentRepository commentRepository;

    @InjectMocks private ItemServiceImpl itemService;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Олег", "o@mail.com");
        item = new Item(1L, "Дрель", "Топ", true, owner, null);
    }

    @Test
    void updateItem_UpdateAllFields_ReturnsDto() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto updateDto = new ItemDto(null, "Новое", "Новое оп", false, null, null, null, null);
        ItemDto result = itemService.updateItem(1L, 1L, updateDto);

        assertEquals("Новое", result.getName());
        assertEquals("Новое оп", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void updateItem_UpdateOnlyName_ReturnsDto() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto updateDto = new ItemDto(null, "Новое", null, null, null, null, null, null);
        ItemDto result = itemService.updateItem(1L, 1L, updateDto);

        assertEquals("Новое", result.getName());
        assertEquals("Топ", result.getDescription());
    }

    @Test
    void getItemById_AsOwner_WithBookings() {
        User booker = new User(2L, "Booker", "b@mail.com");
        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, booker, Status.APPROVED);
        Booking nextBooking = new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item, booker, Status.APPROVED);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstPreviousBooking(any(), any(), any())).thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstNextBooking(any(), any(), any())).thenReturn(Optional.of(nextBooking));

        ItemDto result = itemService.getItemById(1L, 1L); // 1L - владелец
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
    }

    @Test
    void getItemById_NotOwner_NoBookings() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDto result = itemService.getItemById(2L, 1L); // 2L - не владелец
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }
}