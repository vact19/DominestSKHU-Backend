package com.dominest.dominestbackend.domain.post.inspection.floor.repository;

import com.dominest.dominestbackend.domain.post.inspection.floor.entity.InspectionFloor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InspectionFloorRepository extends JpaRepository<InspectionFloor, Long> {

    @Query("SELECT if FROM InspectionFloor if " +
            "where if.inspectionPost.id = :postId")
    List<InspectionFloor> findAllByPostId(@Param("postId") Long postId);
}
