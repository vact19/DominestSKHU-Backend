package com.dominest.dominestbackend.domain.room.roomhistory.service;

import com.dominest.dominestbackend.domain.resident.entity.Resident;
import com.dominest.dominestbackend.domain.resident.repository.ResidentRepository;
import com.dominest.dominestbackend.domain.resident.entity.component.ResidenceSemester;
import com.dominest.dominestbackend.domain.room.roomhistory.entity.RoomHistory;
import com.dominest.dominestbackend.domain.room.roomhistory.repository.RoomHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RoomHistoryService {
    private final RoomHistoryRepository roomHistoryRepository;
    private final ResidentRepository residentRepository;

    @Transactional
    public void saveFrom(Resident resident) {
        RoomHistory roomHistory = RoomHistory.builder()
                .residentName(resident.getPersonalInfo().getName())
                .admissionDate(resident.getResidenceDateInfo().getAdmissionDate())
                .leavingDate(resident.getResidenceDateInfo().getLeavingDate())
                .phoneNumber(resident.getPersonalInfo().getPhoneNumber().getValue())
                .studentId(resident.getStudentInfo().getStudentId())
                .room(resident.getRoom())
                .build();

        roomHistoryRepository.save(roomHistory);
    }

    @Transactional
    public void initRoomHistory(ResidenceSemester residenceSemester) {
        residentRepository.findAllByResidenceSemesterFetchRoom(residenceSemester)
                .forEach(this::saveFrom);
    }
}
