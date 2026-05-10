package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final User owner = new User(1L, "Иван", "o@mail.com");
    private final Item item = new Item(1L, "Дрель", "Топ", true, owner, null);

    @Test
    void createItem_Valid_ReturnsDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.createItem(1L, new ItemDto(null, "Дрель", "Топ", true, null, null, null, null));
        assertEquals(1L, result.getId());
    }

    @Test
    void createItem_WithRequest_ReturnsDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(new ItemRequest()));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto dto = new ItemDto(null, "Дрель", "Топ", true, 1L, null, null, null);
        assertNotNull(itemService.createItem(1L, dto));
    }

    @Test
    void updateItem_NotOwner_ThrowsNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> itemService.updateItem(2L, 1L, new ItemDto()));
    }

    @Test
    void getItemById_IsOwner_ReturnsWithBookings() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of());

        ItemDto result = itemService.getItemById(1L, 1L);
        assertNotNull(result);
    }

    @Test
    void searchItems_BlankText_ReturnsEmptyList() {
        assertTrue(itemService.searchItems("").isEmpty());
    }

    @Test
    void searchItems_ValidText_ReturnsList() {
        when(itemRepository.search(anyString())).thenReturn(List.of(item));
        assertEquals(1, itemService.searchItems("Дрель").size());
    }

    @Test
    void createComment_UserNotEligible_ThrowsValidation() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "крол", "m@m.com")));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.hasCompletedBooking(anyLong(), anyLong(), any(), any())).thenReturn(false);

        assertThrows(ValidationException.class, () -> itemService.createComment(2L, 1L, new CommentDto()));
    }

    @Test
    void createComment_Valid_ReturnsDto() {
        User author = new User(2L, "U", "u@m.com");
        when(userRepository.findById(2L)).thenReturn(Optional.of(author));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.hasCompletedBooking(anyLong(), anyLong(), any(), any())).thenReturn(true);
        when(commentRepository.save(any())).thenReturn(new Comment(1L, "Текст", item, author,
                LocalDateTime.now()));

        assertNotNull(itemService.createComment(2L, 1L, new CommentDto(null, "Текст", null, null)));
    }
}