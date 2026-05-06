package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking as b WHERE b.booker.id = :bookerId ORDER BY b.start DESC")
    List<Booking> findAllBookingByBooker(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking as b WHERE b.booker.id = :bookerId AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastBookingsByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking as b WHERE b.booker.id = :bookerId AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByBooker(@Param("bookerId") Long bookerId,
                                                                   @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking as b WHERE b.booker.id = :bookerId AND b.start <= :now AND b.end >= :now ORDER BY" +
            " b.start DESC")
    List<Booking> findCurrentBookingsByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking as b WHERE b.booker.id = :bookerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findBookingsByBookerAndStatus(@Param("bookerId") Long bookerId, @Param("status") Status status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastBookingsByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start <= :now AND" +
            " b.end >= :now ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findBookingsByOwnerAndStatus(@Param("ownerId") Long ownerId, @Param("status") Status status);

    boolean existsByBookerIdAndItemIdAndEndIsBeforeAndStatus(Long bookerId, Long  itemId,
                                                                                   LocalDateTime now, Status status);

    Optional<Booking> findFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(Long itemId, LocalDateTime now,
                                                                                      Status status);

    Optional<Booking> findFirstByItemIdAndStartGreaterThanEqualAndStatusOrderByStartDesc(Long itemId, LocalDateTime now,
                                                                                         Status status);
}
