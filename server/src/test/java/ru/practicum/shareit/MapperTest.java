package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MapperTest {
    @Test
    void testUserMapper() {
        User user = new User(1L, "алешк", "email");
        UserDto dto = UserMapper.toUserDto(user);
        assertEquals(user.getId(), dto.getId());

        User mappedUser = UserMapper.toUser(dto);
        assertEquals(dto.getId(), mappedUser.getId());
    }

    @Test
    void testItemMapper() {
        assertNull(ItemMapper.toItemDto(null));
        assertNull(ItemMapper.toItem(null, null, null));

        User owner = new User(1L, "Олех", "email");
        ItemRequest req = new ItemRequest(1L, "Топ", owner, LocalDateTime.now());
        Item item = new Item(1L, "Дрель", "Топ", true, owner, req);

        ItemDto dto = ItemMapper.toItemDto(item);
        assertEquals(item.getName(), dto.getName());
        assertEquals(req.getId(), dto.getRequestId());

        Item mappedItem = ItemMapper.toItem(dto, owner, req);
        assertEquals(dto.getName(), mappedItem.getName());
    }

    @Test
    void testBookingMapper() {
        User booker = new User(1L, "Мах", "email");
        Item item = new Item(1L, "дрель", "Топ", true, null, null);
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item, booker, Status.WAITING);

        assertNotNull(BookingMapper.toBookingResponseDto(booking));
    }

    @Test
    void testCommentMapper() {
        User author = new User(1L, "Крол", "email");
        Item item = new Item(1L, "Дрель", "Топ", true, null, null);
        Comment comment = new Comment(1L, "Найс", item, author, LocalDateTime.now());

        assertEquals("Крол", CommentMapper.toCommentDto(comment).getAuthorName());
    }

    @Test
    void testItemRequestMapper() {
        User requestor = new User(1L, "Жмых", "email");
        ItemRequestDto dto = new ItemRequestDto("Топ");

        ItemRequest req = ItemRequestMapper.toItemRequest(dto, requestor);
        assertEquals("Топ", req.getDescription());

        assertNotNull(ItemRequestMapper.toItemRequestResponseDto(req, List.of()));
    }
}