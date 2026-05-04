package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = ItemMapper.toItem(itemDto, owner);
        Item savedItem = itemRepository.save(item);

        return ItemMapper.toItemDto(savedItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такой вещи не существует"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Редактировать может только владелец вещи");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        ItemDto itemDto = ItemMapper.toItemDto(item);

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime created = LocalDateTime.now();

            bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(itemId, created,
                    Status.APPROVED)
                    .ifPresent(last -> itemDto.setLastBooking(new ItemDto.BookingShortDto(last.getId(),
                            last.getBooker().getId())));

            bookingRepository.findFirstByItemIdAndStartGreaterThanEqualAndStatusOrderByStartDesc(itemId, created,
                    Status.APPROVED)
                    .ifPresent(next -> itemDto.setNextBooking(new ItemDto.BookingShortDto(next.getId(),
                            next.getBooker().getId())));
        }

        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        return itemRepository.findAllByOwnerId(userId).stream()
                .map(item -> {
                    ItemDto itemDto = ItemMapper.toItemDto(item);

                    if (item.getOwner().getId().equals(userId)) {
                        LocalDateTime created = LocalDateTime.now();

                        bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(item.getId(),
                                        created, Status.APPROVED)
                                .ifPresent(last -> itemDto.setLastBooking(new ItemDto
                                        .BookingShortDto(last.getId(), last.getBooker().getId())));

                        bookingRepository.findFirstByItemIdAndStartGreaterThanEqualAndStatusOrderByStartDesc(item
                                                .getId(), created, Status.APPROVED)
                                .ifPresent(next -> itemDto.setNextBooking(new ItemDto.BookingShortDto(next.getId(),
                                        next.getBooker().getId())));
                    }
                    return itemDto;
                })
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));

        boolean isLegit = bookingRepository.existsByBookerIdAndItemIdAndEndIsBeforeAndStatus(userId, itemId,
                LocalDateTime.now(), Status.APPROVED);

        if (!isLegit) {
            throw new ValidationException("У вас нет доступа к отзыву, ибо вы не брали данную вещь, " +
                    "либо аренда не завершена");
        }

        Comment comment = new Comment(null, commentDto.getText(), item, author, LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
