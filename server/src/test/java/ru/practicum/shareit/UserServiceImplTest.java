package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_ValidUser_ReturnsSavedUser() {
        UserDto dto = new UserDto(null, "Ваня", "ivan@mail.com");
        User user = new User(1L, "Ваня", "ivan@mail.com");
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.createUser(dto);
        assertEquals("Ваня", result.getName());
    }

    @Test
    void createUser_EmailExists_ThrowsConflict() {
        UserDto dto = new UserDto(null, "Ваня", "ivan@mail.com");
        when(userRepository.existsByEmail(any())).thenReturn(true);
        assertThrows(ConflictException.class, () -> userService.createUser(dto));
    }

    @Test
    void updateUser_UserNotFound_ThrowsNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.updateUser(1L, new UserDto()));
    }

    @Test
    void getUserById_UserFound_ReturnsDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Иван", "e@mail.ru")));
        assertNotNull(userService.getUserById(1L));
    }

    @Test
    void getUserById_UserNotFound_ThrowsNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getAllUsers_ReturnsList() {
        when(userRepository.findAll()).thenReturn(List.of(new User(1L, "Иван", "e@mail.ru")));
        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    void updateUser_Valid_ReturnsDto() {
        User user = new User(1L, "Иван", "old@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto updateDto = new UserDto(null, "Ваня", "new@mail.ru");
        UserDto result = userService.updateUser(1L, updateDto);
        assertEquals("Ваня", result.getName());
    }
}