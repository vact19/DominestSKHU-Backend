package com.dominest.dominestbackend.domain.room.repository;

import com.dominest.dominestbackend.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByAssignedRoom(String assignedRoom);

    List<Room> findByFloorNo(Integer roomNo);
}
