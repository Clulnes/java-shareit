package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime now,
                                                                                 LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId, LocalDateTime now,
                                                                                    LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    boolean existsByBookerIdAndItemIdAndEndIsBeforeAndStatus(Long bookerId, Long  itemId,
                                                                                   LocalDateTime now, Status status);

    Optional<Booking> findFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(Long itemId, LocalDateTime now,
                                                                                      Status status);

    Optional<Booking> findFirstByItemIdAndStartGreaterThanEqualAndStatusOrderByStartDesc(Long itemId, LocalDateTime now,
                                                                                         Status status);
}
