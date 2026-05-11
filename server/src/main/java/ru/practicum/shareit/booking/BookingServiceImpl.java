package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public BookingResponseDto createBooking(Long userId, BookingDto dto) {
        if (dto.getEnd().isBefore(dto.getStart()) || dto.getEnd().equals(dto.getStart())) {
            throw new ValidationException("Дата окончания не может быть раньше или равна дате начала");
        }

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою же вещь");
        }

        Booking booking = new Booking(null, dto.getStart(), dto.getEnd(), item, booker, Status.WAITING);

        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID: " + bookingId + " не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Только владелец вещи может подтвердить/отклонить бронирование");
        }

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Бронирование уже подтверждено");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID: " + bookingId + " не найдено"));

        boolean isBooker =  booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!isBooker && !isOwner) {
            throw  new NotFoundException("У вас нет прав на просмотр данного бронирования");
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllByBooker(Long userId, String stateString) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует");
        }

        BookingState state = parseState(stateString);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByBooker(userId, now);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByBooker(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByBooker(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByBookerAndStatus(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByBookerAndStatus(userId, Status.REJECTED);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findAllBookingByBooker(userId);
                break;
        }

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllByOwner(Long ownerId, String stateString) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        BookingState state = parseState(stateString);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByOwner(ownerId, now);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByOwner(ownerId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByOwner(ownerId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByOwnerAndStatus(ownerId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByOwnerAndStatus(ownerId, Status.REJECTED);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findAllBookingsByOwner(ownerId);
                break;
        }

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    private  BookingState parseState(String stateString) {
        try  {
            return BookingState.valueOf(stateString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Неизвестный статус: " +  stateString);
        }
    }
}
