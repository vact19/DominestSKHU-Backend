package com.dominest.dominestbackend.domain.room.roomhistory.repository;

import com.dominest.dominestbackend.domain.room.roomhistory.entity.RoomHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomHistoryRepository extends JpaRepository<RoomHistory, Long> {
}