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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
}