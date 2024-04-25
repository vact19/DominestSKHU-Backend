package com.dominest.dominestbackend.domain.resident.repository;

import com.dominest.dominestbackend.domain.resident.entity.component.ResidenceSemester;
import com.dominest.dominestbackend.domain.resident.entity.Resident;
import com.dominest.dominestbackend.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResidentRepository extends JpaRepository<Resident, Long> {
    @Query("SELECT r FROM Resident r" +
            " join fetch r.room" +
            " where r.residenceSemester = :residenceSemester")
    List<Resident> findAllByResidenceSemesterFetchRoom(@Param("residenceSemester") ResidenceSemester residenceSemester);

    Resident findByPersonalInfoNameAndResidenceSemester(String name, ResidenceSemester residenceSemester);

    Optional<Resident> findByResidenceSemesterAndRoom(ResidenceSemester residenceSemester, Room room);

    List<Resident> findAllByResidenceSemester(ResidenceSemester semester);
}
