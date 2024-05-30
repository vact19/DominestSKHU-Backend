package com.dominest.dominestbackend.domain.room.service;

import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.room.entity.Room;
import com.dominest.dominestbackend.domain.room.repository.RoomRepository;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public Room getByAssignedRoom(String assignedRoom) {
        return roomRepository.findByAssignedRoom(assignedRoom)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.ROOM, "방 코드", assignedRoom));
    }

    public Room getById(long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.ROOM, id));
    }

    public List<Room> findByFloorNo(int floorNo) {
        return roomRepository.findByFloorNo(floorNo);
    }
}
