package com.example.hotel_management.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;

import com.example.hotel_management.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId")
    List<Booking> findByRoomId(Long roomId);

    @Query("SELECT b FROM Booking b WHERE b.bookingConfirmationCode = :code")
    List<Booking> findByBookingConfirmationCode(String code);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId")
    List<Booking> findByUserId(Long userId);
}
