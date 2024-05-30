package com.dominest.dominestbackend.domain.post.inspection.floor.room.component;

import com.dominest.dominestbackend.domain.resident.entity.Resident;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * 입사자의 이름, 학번, 전화번호를 담는 Embeddable 클래스
 * 점검대상 방과 입사자정보를 담는 InspectionRoom 에서 사용한다.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Embeddable
public class InspectionResidentInfo {
    String name;
    String studentId;
    String phoneNo;

    public static InspectionResidentInfo from(Resident resident){
        return new InspectionResidentInfo(
                resident.getPersonalInfo().getName()
                , resident.getStudentInfo().getStudentId()
                , resident.getPersonalInfo().getPhoneNumber().getValue());
    }
}
