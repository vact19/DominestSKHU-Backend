package com.dominest.dominestbackend.domain.room;

import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.global.exception.exceptions.external.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
