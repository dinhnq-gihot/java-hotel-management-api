package com.example.hotel_management.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tables")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate check_in_date;
    private LocalDate check_out_date;

    private int num_of_adults;
    private int num_of_children;
    private int total_num_of_guest;
    private String bookingConfirmationCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    // private Room room

    @Override
    public String toString() {
        return "Booking [id=" + id + ", check_in_date=" + check_in_date + ", check_out_date=" + check_out_date
                + ", num_of_adults=" + num_of_adults + ", num_of_children=" + num_of_children + ", total_num_of_guest="
                + total_num_of_guest + ", bookingConfirmationCode=" + bookingConfirmationCode + ", user=" + user + "]";
    }

}
