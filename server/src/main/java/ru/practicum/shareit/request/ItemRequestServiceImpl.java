package ru.practicum.shareit.request;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public ItemRequestResponseDto createRequest(Long userId, ItemRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = ItemRequestMapper.toItemRequest(dto, user);
        request = itemRequestRepository.save(request);

        return ItemRequestMapper.toItemRequestResponseDto(request, List.of());
    }

    @Override
    public List<ItemRequestResponseDto> getUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestor_Id(userId,
                Sort.by(Sort.Direction.DESC, "created"));

        return mapRequestsToDto(requests);
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestor_IdNot(
                userId, Sort.by(Sort.Direction.DESC, "created"));

        return mapRequestsToDto(requests);
    }

    @Override
    public ItemRequestResponseDto getRequestById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        List<Item> items = itemRepository.findAllByRequest_Id(requestId);

        return ItemRequestMapper.toItemRequestResponseDto(request, items);
    }

    private List<ItemRequestResponseDto> mapRequestsToDto(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<Item> allItems = itemRepository.findAllByRequest_IdIn(requestIds);

        return requests.stream()
                .map(request -> {
                    List<Item> requestItems = allItems.stream()
                            .filter(item -> item.getRequest().getId() != null && item.getRequest().getId()
                                    .equals(request.getId()))
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestResponseDto(request, requestItems);
                })
                .collect(Collectors.toList());
    }
}
