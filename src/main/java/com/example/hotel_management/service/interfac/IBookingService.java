package com.example.hotel_management.service.interfac;

import com.example.hotel_management.dto.Response;
import com.example.hotel_management.entity.Booking;

public interface IBookingService {
    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);

    Response findBookingByConfirmationCode(String confirmationCode);

    Response getAllBookings();

    Response cancelBooking(Long bookingId);
}
