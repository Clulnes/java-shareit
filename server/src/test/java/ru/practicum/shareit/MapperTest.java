package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemMapperTest {
    @Test
    void toItemDto_NullInput_ReturnsNull() {
        assertNull(ItemMapper.toItemDto(null));
    }

    @Test
    void toItem_ValidInput_ReturnsItem() {
        ItemDto dto = new ItemDto(1L, "Дрель", "Топ", true, null, null, null, null);
        Item item = ItemMapper.toItem(dto, new User(1L, "Тест", "e@m.com"), null);
        assertEquals("Дрель", item.getName());
    }
}