package com.dominest.dominestbackend.domain.room.support;

import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.room.entity.Room;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomSearchMap {
    private final Map<String, Room> assignedRoomSearchMap;

    public static RoomSearchMap from(List<Room> rooms) {
        return new RoomSearchMap(rooms);
    }

    private RoomSearchMap(List<Room> rooms) {
        this.assignedRoomSearchMap = rooms
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        Room::getAssignedRoom
                        , room -> room)
                );
    }

    public Room getByAssignedRoom(String assignedRoom) {
        Room room = assignedRoomSearchMap.get(assignedRoom);
        if (room == null) {
            throw new ResourceNotFoundException(Datasource.ROOM, "방 코드", assignedRoom);
        }
        return room;
    }
}
