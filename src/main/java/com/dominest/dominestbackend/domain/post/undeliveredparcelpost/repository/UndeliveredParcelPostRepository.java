package com.dominest.dominestbackend.domain.post.undeliveredparcelpost.repository;

import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.entity.UndeliveredParcelPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UndeliveredParcelPostRepository extends JpaRepository<UndeliveredParcelPost, Long> {

    @Query(value = "SELECT p FROM UndeliveredParcelPost p" +
            " WHERE p.category.id = :categoryId"
            , countQuery = "SELECT count(p) FROM UndeliveredParcelPost p WHERE p.category.id = :categoryId")
    Page<UndeliveredParcelPost> findAllByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM UndeliveredParcelPost p" +
            " LEFT JOIN FETCH p.undelivParcels" +
            " WHERE p.id = :postId")
    Optional<UndeliveredParcelPost> findByIdFetchParcels(@Param("postId") Long undelivParcelPostId);

    void deleteByCategoryId(Long categoryId);
}
