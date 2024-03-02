package com.dominest.dominestbackend.domain.post.sanitationcheck.floor.checkedroom;

import com.dominest.dominestbackend.api.post.sanitationcheck.request.UpdateCheckedRoomRequest;
import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.global.exception.exceptions.domain.DomainException;
import com.dominest.dominestbackend.global.exception.exceptions.external.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CheckedRoomService {
    private final CheckedRoomRepository checkedRoomRepository;

    @Transactional
    public List<CheckedRoom> create(List<CheckedRoom> checkedRooms) {
        try {
            return checkedRoomRepository.saveAll(checkedRooms);
        } catch (DataIntegrityViolationException e) {
            throw new DomainException("CheckedRoom 저장 실패, 중복 혹은 값의 누락을 확인해주세요"
                    , HttpStatus.BAD_REQUEST, e);
        }
    }

    public CheckedRoom getById(Long id) {
        return checkedRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.CHECKED_ROOM, id));
    }

    public List<CheckedRoom> getAllByFloorId(Long floorId) {
        return checkedRoomRepository.findAllByFloorIdFetchResidentAndRoom(floorId);
    }

    @Transactional
    public void update(Long checkedRoomId, UpdateCheckedRoomRequest request) { // api 호출 편의성을 위해 이 ReqDto는 값 검증하지 않았음.
        CheckedRoom checkedRoom = getById(checkedRoomId);
        // Null이 아닌 값만 업데이트
        checkedRoom.updateValuesOnlyNotNull(
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
        CheckedRoom checkedRoom = getById(roomId);
        checkedRoom.passAll();
    }
}














