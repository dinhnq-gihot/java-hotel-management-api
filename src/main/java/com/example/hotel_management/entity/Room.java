package com.example.hotel_management.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookingConfirmationCode;
    private String roomType;
    private String roomPrice;
    private String roomPhotoUrl;
    private String roomDescription;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    @Override
    public String toString() {
        return "Room [id=" + id + ", bookingConfirmationCode=" + bookingConfirmationCode + ", roomType="
                + roomType + ", roomPrice=" + roomPrice + ", roomPhotoUrl=" + roomPhotoUrl
                + ", roomDescription=" + roomDescription + ", bookings=" + bookings + "]";
    }
}
