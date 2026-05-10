package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = ShareItGateway.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserClient userClient;

    @Test
    void createUser_whenValid_thenReturnOk() throws Exception {
        UserDto userDto = new UserDto(null, "Артур", "artur@mail.com");

        when(userClient.createUser(any())).thenReturn(ResponseEntity.ok(userDto));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    void createUser_whenEmailInvalid_thenReturnBadRequest() throws Exception {
        UserDto userDto = new UserDto(null, "Мария", "плохая-почта");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_whenValid_thenReturnOk() throws Exception {
        when(userClient.getUser(anyLong())).thenReturn(ResponseEntity.ok(new UserDto()));

        mvc.perform(get("/users/1")).andExpect(status().isOk());
    }
}