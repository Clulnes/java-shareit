package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@ContextConfiguration(classes = ShareItServer.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem() throws Exception {
        ItemDto dto = new ItemDto(1L, "Дрель", "Мощь", true, null, null, null, null);
        when(itemService.createItem(anyLong(), any())).thenReturn(dto);
        mvc.perform(post("/items").header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(new ItemDto());
        mvc.perform(get("/items/1").header("X-Sharer-User-Id", 1)).andExpect(status().isOk());
    }

    @Test
    void searchItems() throws Exception {
        when(itemService.searchItems(any())).thenReturn(List.of(new ItemDto()));
        mvc.perform(get("/items/search?text=N").header("X-Sharer-User-Id", 1)).andExpect(status().isOk());
    }
}