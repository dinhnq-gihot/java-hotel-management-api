package com.example.hotel_management.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tables")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "check in date is required")
    private LocalDate checkInDate;
    @Future(message = "check out date must be in the future")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "Number of adults must not be less than 1")
    private int numOfAdults;
    @Min(value = 0, message = "Number of children must not be less than 0")
    private int numOfChildren;
    private int totalNumOfGuests;
    private String bookingConfirmationCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Override
    public String toString() {
        return "Booking [id=" + id + ", checkInDate=" + checkInDate + ", checkOutDate=" + checkOutDate
                + ", numOfAdults=" + numOfAdults + ", numOfChildren=" + numOfChildren + ", totalNumOfGuest="
                + totalNumOfGuests + ", bookingConfirmationCode=" + bookingConfirmationCode + ", user=" + user
                + "]";
    }

    public void calculateTotalNumberOfGuests() {
        this.totalNumOfGuests = this.numOfAdults + this.numOfChildren;
    }

    public void setNumOfAdults(int num) {
        this.numOfAdults = num;
        calculateTotalNumberOfGuests();
    }

    public void setNumOfChildren(int num) {
        this.numOfChildren = num;
        calculateTotalNumberOfGuests();
    }
}
