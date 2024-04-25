package com.dominest.dominestbackend.domain.post.inspection.floor.room.repository;

import com.dominest.dominestbackend.domain.post.inspection.floor.room.entity.InspectionRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InspectionRoomRepository extends JpaRepository<InspectionRoom, Long> {
    // InspectionRoom 의 Resident Id는 Null일 수 있다. 모든 InspectionRoom 을 조회해야 하므로 Left Join Resident로 조회한다.
    @Query("SELECT ir FROM InspectionRoom ir " +
            "JOIN FETCH ir.room " +
            "WHERE ir.inspectionFloor.id = :inspectionFloorId")
    List<InspectionRoom> findAllByFloorIdFetchResidentAndRoom(@Param("inspectionFloorId") Long inspectionFloorId);

    // 방역점검 게시글에 연관된 모든 InspectionRoom을 가져온다. 3중 조인해야 하며, 미통과만 조회한다,
    //모든 InspectionRoom을 조회해야 하므로 Left Join Resident로 조회한다.
    @Query("SELECT ir FROM InspectionRoom ir" +
            " JOIN ir.inspectionFloor f" +
            " JOIN FETCH ir.room" +
            " WHERE f.inspectionPost.id = :postId" +
            " AND ir.passState = :passState")
    List<InspectionRoom> findNotPassedAllByPostId(@Param("postId") Long postId, @Param("passState") InspectionRoom.PassState passState);

    // InspectionPost Id에 속한 InspectionRoom 전체를 조회한다.
    //모든 InspectionRoom을 조회해야 하므로 Left Join Resident로 조회한다.
    @Query("SELECT ir FROM InspectionRoom ir" +
            " JOIN ir.inspectionFloor f" + // inspectionFloor를 거쳐야 inspectionPost에 접근할 수 있다. inspectionFloor 데이터가 필요하지는 않으므로 fetch하지 않는다.
            " JOIN FETCH ir.room ro" +

            " WHERE f.inspectionPost.id = :postId")
    List<InspectionRoom> findAllByPostId(@Param("postId")Long postId);

    // InspectionPost Id에 속한 InspectionRoom 전체를 조회한다.
    // 빈 방은 조회하지 않을 것이므로 Inner Join Resident로 조회한다.
    // Resident와 Room을 Fetch Join하며, passState를 Not In으로 조회한다.
    @Query("SELECT ir FROM InspectionRoom ir" +
            " JOIN ir.inspectionFloor f" + // inspectionFloor를 거쳐야 inspectionPost에 접근할 수 있다. inspectionFloor 데이터가 필요하지는 않으므로 fetch하지 않는다.
            " JOIN FETCH ir.room ro" +

            " WHERE f.inspectionPost.id = :postId" +
            " AND ir.passState NOT IN :passStates")
    List<InspectionRoom> findAllByPostIdAndNotInPassState(@Param("postId")Long postId, @Param("passStates")List<InspectionRoom.PassState> passStates);

    // InspectionPost Id에 속한 InspectionRoom 전체를 조회한다.
    // 빈 방은 조회하지 않을 것이므로 Inner Join Resident로 조회한다.
    // Resident와 Room을 Fetch Join한다.
    @Query("SELECT ir FROM InspectionRoom ir" +
            " JOIN ir.inspectionFloor if" + // inspectionFloor를 거쳐야 inspectionPost에 접근할 수 있다. inspectionFloor 데이터가 필요하지는 않으므로 fetch하지 않는다.
            " JOIN FETCH ir.room r" +
            " WHERE if.inspectionPost.id = :postId" +
            " AND ir.passState = :passState")
    List<InspectionRoom> findAllByPostIdAndPassState(@Param("postId")Long postId, @Param("passState") InspectionRoom.PassState passState);
}
