package com.dominest.dominestbackend.domain.resident;

import com.dominest.dominestbackend.domain.resident.component.ResidenceSemester;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResidentSearchMap {
    private final Map<String, Resident> sameNameSearchMap;
    private final Map<String, Resident> samePersonSearchMap;

    public static ResidentSearchMap from(List<Resident> residents) {
        return new ResidentSearchMap(residents);
    }

    private ResidentSearchMap(List<Resident> residents) {
        this.sameNameSearchMap = residents
                .stream()
                .collect(Collectors.toMap(
                        resident -> resident.getResidenceSemester() + resident.getPersonalInfo().getName()
                        , resident -> resident)
                );
        this.samePersonSearchMap = residents.stream()
                .collect(Collectors.toMap(
                        resident -> resident.getResidenceSemester()
                                + resident.getStudentInfo().getStudentId()
                                + resident.getPersonalInfo().getPhoneNumber().getValue()
                        , resident -> resident)
                );
    }
    /** 학기 내 동명이인 검사용 */
    public boolean existsSameNameInSemester(ResidenceSemester residenceSemester, String name) {
        return sameNameSearchMap.containsKey(residenceSemester + name);
    }

    /** 학기 내 동일인 검사용 */
    public boolean existsSameResidentInSemester(Resident resident) {
        return samePersonSearchMap.containsKey(
                resident.getResidenceSemester()
                        + resident.getStudentInfo().getStudentId()
                        + resident.getPersonalInfo().getPhoneNumber().getValue()
        );
    }

    public void add(Resident resident) {
        sameNameSearchMap.put(resident.getResidenceSemester() + resident.getPersonalInfo().getName(), resident);
        samePersonSearchMap.put(
                resident.getResidenceSemester()
                        + resident.getStudentInfo().getStudentId()
                        + resident.getPersonalInfo().getPhoneNumber().getValue()
                        + resident.getPersonalInfo().getName()
                , resident);
    }
}
