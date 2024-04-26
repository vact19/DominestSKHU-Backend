package com.dominest.dominestbackend.domain.resident.support;

import com.dominest.dominestbackend.domain.resident.entity.Resident;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResidentSearchMap {
    private final Map<String, Resident> sameNameSearchMap;
    private final Map<String, Resident> samePersonSearchMap;
    private final Map<Long, Resident> sameRoomSearchMap;

    public static ResidentSearchMap from(List<Resident> residents) {
        return new ResidentSearchMap(residents);
    }

    private ResidentSearchMap(List<Resident> residents) {
        this.sameNameSearchMap = residents
                .stream()
                .collect(Collectors.toMap(
                        resident -> resident.getPersonalInfo().getName()
                        , resident -> resident)
                );
        this.samePersonSearchMap = residents.stream()
                .collect(Collectors.toMap(
                        resident ->
                                resident.getStudentInfo().getStudentId()
                                + resident.getPersonalInfo().getPhoneNumber().getValue()
                        , resident -> resident)
                );
        this.sameRoomSearchMap = residents.stream()
                .collect(Collectors.toMap(
                        resident -> resident.getRoom().getId()
                        , resident -> resident)
                );
    }

    /** 학기 내 동명이인 검사용 */
    public boolean existsSameName(String name) {
        return sameNameSearchMap.containsKey(name);
    }

    /** 학기 내 동일인 검사용 */
    public boolean existsSameResident(Resident resident) {
        return samePersonSearchMap.containsKey(
                resident.getStudentInfo().getStudentId()
                + resident.getPersonalInfo().getPhoneNumber().getValue()
        );
    }

    public boolean existsSameRoom(Long roomId) {
        return sameRoomSearchMap.containsKey(roomId);
    }

    public void add(Resident resident) {
        sameNameSearchMap.put(resident.getPersonalInfo().getName(), resident);
        samePersonSearchMap.put(
                resident.getStudentInfo().getStudentId()
                + resident.getPersonalInfo().getPhoneNumber().getValue()
                + resident.getPersonalInfo().getName()
                , resident);
        sameRoomSearchMap.put(resident.getRoom().getId(), resident);
    }
}
