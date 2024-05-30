package com.dominest.dominestbackend.domain.post.inspection.repository;

import com.dominest.dominestbackend.domain.post.inspection.entity.InspectionPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InspectionPostRepository extends JpaRepository<InspectionPost, Long> {

    @Query(value = "SELECT ip FROM InspectionPost ip" +
            " WHERE ip.category.id = :categoryId"
            , countQuery = "SELECT count(ip) FROM InspectionPost ip WHERE ip.category.id = :categoryId")
    Page<InspectionPost> findAllByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query(value = "SELECT ip FROM InspectionPost ip" +
            " JOIN FETCH ip.category" +
            " WHERE ip.id = :postId")
    Optional<InspectionPost> findByIdFetchCategory(@Param("postId") Long postId);
}
