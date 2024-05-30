package com.dominest.dominestbackend.domain.room.roomhistory.repository;

import com.dominest.dominestbackend.domain.room.roomhistory.entity.RoomHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomHistoryRepository extends JpaRepository<RoomHistory, Long> {
    @Query("SELECT rh FROM RoomHistory rh" +
            " WHERE rh.room.id = :roomId" +
            " ORDER BY rh.id DESC")
    List<RoomHistory> findByRoomId(@Param("roomId") long roomId);
}