package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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
}