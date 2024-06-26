package com.dominest.dominestbackend.domain.post.image.repository;

import com.dominest.dominestbackend.domain.post.image.entity.ImageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ImageTypeRepository extends JpaRepository<ImageType, Long> {
    @Query("SELECT i FROM ImageType i JOIN FETCH i.writer LEFT JOIN FETCH i.imageUrls WHERE i.id = :imageTypeId")
    Optional<ImageType> findByIdFetchWriterAndImageUrls(@Param("imageTypeId") Long imageTypeId);

    @Query(value = "SELECT i FROM ImageType i" +
            " WHERE i.category.id = :categoryId" +
            " ORDER BY i.id DESC"
            , countQuery = "SELECT count(i) FROM ImageType i WHERE i.category.id = :categoryId")
    Page<ImageType> findAllByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT i FROM ImageType i" +
            " LEFT JOIN FETCH i.imageUrls" +
            " WHERE i.id = :imageTypeId")
    Optional<ImageType> findByIdFetchImageUrls(@Param("imageTypeId") Long imageTypeId);

    void deleteByCategoryId(Long categoryId);
}
