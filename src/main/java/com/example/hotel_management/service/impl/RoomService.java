package com.example.hotel_management.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.hotel_management.dto.Response;
import com.example.hotel_management.dto.RoomDTO;
import com.example.hotel_management.entity.Room;
import com.example.hotel_management.exception.OurException;
import com.example.hotel_management.repo.RoomRepository;
import com.example.hotel_management.service.AwsS3Service;
import com.example.hotel_management.service.interfac.IRoomService;
import com.example.hotel_management.utils.Utils;

@Service
public class RoomService implements IRoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AwsS3Service awsS3Service;

    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        Response res = new Response();

        try {
            String imageUrl = awsS3Service.saveImageToS3(photo);
            Room room = new Room();

            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);
            room.setRoomDescription(description);
            room.setRoomPhotoUrl(imageUrl);

            Room saveRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(saveRoom);

            res.setStatusCode(200);
            res.setMessage("Room added successfully");
            res.setRoom(roomDTO);

        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage(e.getMessage());
        }

        return res;
    }

    @Override
    public Response getAllRoomTypes() {
        Response res = new Response();

        try {
            List<String> roomTypes = roomRepository.findDistinctRoomTypes();

            res.setRoomTypeList(roomTypes);
            res.setStatusCode(200);
            res.setMessage("success");

        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage(e.getMessage());
        }

        return res;
    }

    @Override
    public Response getAllRooms() {
        Response res = new Response();

        try {
            List<Room> rooms = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<RoomDTO> roomDTOs = Utils.mapRoomListEntityToRoomListDTO(rooms);

            res.setMessage("success");
            res.setStatusCode(200);
            res.setRoomList(roomDTOs);
            res.setRoomList(roomDTOs);
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error getting all rooms " + e.getMessage());
        }
        return res;
    }

    @Override
    public Response deleteRoom(Long roomId) {
        Response response = new Response();

        try {
            roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            roomRepository.deleteById(roomId);

            response.setMessage("successful");
            response.setStatusCode(200);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting a room " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response updateRoom(Long roomId, String description, String roomType, BigDecimal roomPrice,
            MultipartFile photo) {
        Response res = new Response();

        try {
            String imageUrl = null;

            if (photo != null && !photo.isEmpty()) {
                imageUrl = awsS3Service.saveImageToS3(photo);
            }

            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not found"));
            if (roomType != null) {
                room.setRoomType(roomType);
            }
            if (description != null) {
                room.setRoomDescription(description);
            }
            if (roomPrice != null) {
                room.setRoomPrice(roomPrice);
            }
            if (imageUrl != null)
                room.setRoomPhotoUrl(imageUrl);

            Room updatedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);

            res.setMessage("successful");
            res.setStatusCode(200);
            res.setRoom(roomDTO);

        } catch (OurException e) {
            res.setStatusCode(404);
            res.setMessage(e.getMessage());

        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error updating a room " + e.getMessage());

        }
        return res;
    }

    @Override
    public Response getRoomById(Long roomId) {
        Response response = new Response();

        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);

            response.setMessage("success");
            response.setStatusCode(200);
            response.setRoom(roomDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Getting a room By Id " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        Response response = new Response();

        try {
            List<Room> availableRooms = roomRepository.findByRoomTypeAndBookingsCheckInDateBetween(checkInDate,
                    checkOutDate,
                    roomType);
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(availableRooms);

            response.setMessage("success");
            response.setStatusCode(200);
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting available rooms " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response getAllAvailableRooms() {
        Response response = new Response();

        try {
            List<Room> roomList = roomRepository.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setMessage("successful");
            response.setStatusCode(200);
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting available rooms " + e.getMessage());

        }
        return response;
    }

}
