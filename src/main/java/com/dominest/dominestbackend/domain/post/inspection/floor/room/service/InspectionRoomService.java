package com.dominest.dominestbackend.domain.post.inspection.floor.room.service;

import com.dominest.dominestbackend.api.post.inspection.request.UpdateInspectionRoomRequest;
import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.post.inspection.floor.room.entity.InspectionRoom;
import com.dominest.dominestbackend.domain.post.inspection.floor.room.repository.InspectionRoomRepository;
import com.dominest.dominestbackend.global.exception.exceptions.business.BusinessException;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class InspectionRoomService {
    private final InspectionRoomRepository inspectionRoomRepository;

    @Transactional
    public List<InspectionRoom> create(List<InspectionRoom> inspectionRooms) {
        try {
            return inspectionRoomRepository.saveAll(inspectionRooms);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("InspectionRoom 저장 실패, 중복 혹은 값의 누락을 확인해주세요"
                    , HttpStatus.BAD_REQUEST, e);
        }
    }

    public InspectionRoom getById(Long id) {
        return inspectionRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.INSPECTION_ROOM, id));
    }

    public List<InspectionRoom> getAllByFloorId(Long floorId) {
        return inspectionRoomRepository.findAllByFloorIdFetchResidentAndRoom(floorId);
    }

    @Transactional
    public void update(Long inspectionRoomId, UpdateInspectionRoomRequest request) { // api 호출 편의성을 위해 이 ReqDto는 값 검증하지 않았음.
        InspectionRoom inspectionRoom = getById(inspectionRoomId);
        // Null이 아닌 값만 업데이트
        inspectionRoom.updateValuesOnlyNotNull(
                request.getIndoor()
                , request.getLeavedTrash()
                , request.getToilet()
                , request.getShower()
                , request.getProhibitedItem()
                , request.getPassState()
                , request.getEtc()
        );
    }

    @Transactional
    public void passAll(Long roomId) {
        InspectionRoom inspectionRoom = getById(roomId);
        inspectionRoom.passAll();
    }
}
