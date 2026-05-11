package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto createRequest(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                @RequestBody ItemRequestDto dto) {
        log.info("Создание запроса пользователем {}", userId);
        return itemRequestService.createRequest(userId, dto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getUserRequests(@RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("Получение запросов пользователя {}", userId);
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequests(@RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("Получение всех запросов пользователем {}", userId);
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Получение запроса {} пользователем {}", requestId, userId);
        return itemRequestService.getRequestById(userId, requestId);
    }
}
