package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private final User user = new User(1L, "Ванька", "u@m.com");
    private final ItemRequest request = new ItemRequest(1L, "Дрель", user, LocalDateTime.now());

    @Test
    void createRequest_Valid_ReturnsDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(request);

        assertNotNull(requestService.createRequest(1L, new ItemRequestDto("Дрель")));
    }

    @Test
    void getUserRequests_Valid_ReturnsList() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findAllByRequestor_Id(anyLong(), any())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequest_IdIn(anyList())).thenReturn(List.of());

        assertEquals(1, requestService.getUserRequests(1L).size());
    }

    @Test
    void getUserRequests_UserNotFound_ThrowsNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> requestService.getUserRequests(1L));
    }

    @Test
    void getAllRequests_Valid_ReturnsList() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findAllByRequestor_IdNot(eq(1L), any(Sort.class))).thenReturn(List.of(request));
        when(itemRepository.findAllByRequest_IdIn(anyList())).thenReturn(List.of());

        List<ItemRequestResponseDto> result = requestService.getAllRequests(1L);
        assertEquals(1, result.size());
    }

    @Test
    void getAllRequests_UserNotFound_ThrowsNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> requestService.getAllRequests(1L));
    }

    @Test
    void getRequestById_RequestNotFound_ThrowsNotFound() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(1L, 1L));
    }
}