package com.dominest.dominestbackend.domain.post.complaint.repository;

import com.dominest.dominestbackend.domain.post.complaint.entity.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    @Query(value = "SELECT c FROM Complaint c" +
            " WHERE c.category.id = :categoryId"

            , countQuery = "SELECT count(c) FROM Complaint c WHERE c.category.id = :categoryId"
    )
    Page<Complaint> findPageAllByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT * FROM complaint c" +
            " WHERE c.category_id = :categoryId AND MATCH(complaint_cause, complaint_resolution) AGAINST(:complaintSearch IN BOOLEAN MODE)"

            , countQuery = "SELECT count(*) FROM complaint c WHERE c.category_id = :categoryId AND MATCH(complaint_cause, complaint_resolution) AGAINST(:complaintSearch IN BOOLEAN MODE)"
    )
    Page<Complaint> findAllByCategoryIdSearch(@Param("categoryId")Long categoryId, @Param("complaintSearch") String complaintSearch, Pageable pageable);

    @Query(value = "SELECT c FROM Complaint c" +
            " WHERE c.category.id = :categoryId AND c.roomNo = :roomNoSearch"

            , countQuery = "SELECT count(c) FROM Complaint c WHERE c.category.id = :categoryId AND c.roomNo = :roomNoSearch"
    )
    Page<Complaint> findAllByCategoryIdAndRoomNo(@Param("categoryId") Long categoryId, @Param("roomNoSearch") String roomNoSearch, Pageable pageable);

    void deleteByCategoryId(Long categoryId);

    List<Complaint> findAllByCategoryId(Long categoryId, Pageable pageable);
    List<Complaint> findAllByCategoryId(Long categoryId, Sort sort);
    long countByCategoryId(Long categoryId);
}
