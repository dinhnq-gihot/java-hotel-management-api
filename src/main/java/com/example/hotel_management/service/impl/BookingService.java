package com.example.hotel_management.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hotel_management.dto.BookingDTO;
import com.example.hotel_management.dto.Response;
import com.example.hotel_management.entity.Booking;
import com.example.hotel_management.entity.Room;
import com.example.hotel_management.entity.User;
import com.example.hotel_management.exception.OurException;
import com.example.hotel_management.repo.BookingRepository;
import com.example.hotel_management.repo.RoomRepository;
import com.example.hotel_management.repo.UserRepository;
import com.example.hotel_management.service.interfac.IBookingService;
import com.example.hotel_management.utils.Utils;
import org.springframework.data.domain.Sort;

@Service
public class BookingService implements IBookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        Response res = new Response();

        try {
            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
                throw new IllegalAccessException("Check in date must come before checkout date");
            }
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
            User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User not found"));

            List<Booking> existingBookings = room.getBookings();
            if (!roomIsAvailable(bookingRequest, existingBookings)) {
                throw new OurException("Room not Available for the selected date range");
            }

            Booking booking = new Booking();
            booking.setCheckInDate(bookingRequest.getCheckInDate());
            booking.setCheckOutDate(bookingRequest.getCheckOutDate());
            booking.setRoom(room);
            booking.setUser(user);
            booking.setBookingConfirmationCode(Utils.generateRandomAlphanumeric(10));

            Booking saveBooking = bookingRepository.save(booking);
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTO(saveBooking);

            res.setStatusCode(200);
            res.setMessage("Booking saved successfully");
            res.setBooking(bookingDTO);

        } catch (OurException e) {
            res.setStatusCode(400);
            res.setMessage(e.getMessage());
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage(e.getMessage());
        }

        return res;
    }

    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {
        Response res = new Response();

        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode)
                    .orElseThrow(() -> new OurException("confirmation code not exist"));
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTO(booking);

            res.setStatusCode(200);
            res.setMessage("success");
            res.setBooking(bookingDTO);
        } catch (OurException e) {
            res.setStatusCode(404);
            res.setMessage(e.getMessage());
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error getting booking by confirmation code " + e.getMessage());
        }
        return res;
    }

    @Override
    public Response getAllBookings() {
        Response response = new Response();

        try {
            List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<BookingDTO> bookingDTOList = Utils.mapBookingListEntityToBookingListDTO(bookingList);
            response.setMessage("successful");
            response.setStatusCode(200);
            response.setBookingList(bookingDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all bookings " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response cancelBooking(Long bookingId) {
        Response response = new Response();

        try {
            bookingRepository.findById(bookingId).orElseThrow(() -> new OurException("Booking Not Found"));
            bookingRepository.deleteById(bookingId);
            response.setMessage("successful");
            response.setStatusCode(200);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error cancelling a bookings " + e.getMessage());
        }
        return response;
    }

    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking -> bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                        || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                        || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                        || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                        || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                        || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                        || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate())));

    }
}
