package com.dominest.dominestbackend.domain.post.inspection.floor.service;

import com.dominest.dominestbackend.domain.post.inspection.floor.entity.InspectionFloor;
import com.dominest.dominestbackend.domain.post.inspection.floor.repository.InspectionFloorRepository;
import com.dominest.dominestbackend.global.exception.exceptions.business.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class InspectionFloorService {
    private final InspectionFloorRepository inspectionFloorRepository;

    @Transactional
    public List<InspectionFloor> create(List<InspectionFloor> inspectionFloors) {
        try {
            return inspectionFloorRepository.saveAll(inspectionFloors);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Floor 저장 실패, 중복 혹은 값의 누락을 확인해주세요"
                    , HttpStatus.BAD_REQUEST, e);
        }
    }

    public List<InspectionFloor> getAllByPostId(Long postId) {
        return inspectionFloorRepository.findAllByPostId(postId);
    }
}
