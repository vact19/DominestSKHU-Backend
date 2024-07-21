package com.dominest.dominestbackend.domain.post.image.repository;

import com.dominest.dominestbackend.domain.post.image.entity.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImageTypeRepository extends JpaRepository<ImageType, Long> {
    @Query("SELECT i FROM ImageType i JOIN FETCH i.writer LEFT JOIN FETCH i.imageUrls WHERE i.id = :imageTypeId")
    Optional<ImageType> findByIdFetchWriterAndImageUrls(@Param("imageTypeId") Long imageTypeId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM image_type" +
                    " JOIN (" +
                        " SELECT id FROM image_type i" +
                        " WHERE i.category_id = :categoryId" +
                        " ORDER BY i.id DESC" +
                        " LIMIT :limit OFFSET :offset" +
                    ") as image_type_ids" +
                    " ON image_type.id = image_type_ids.id"
    )
    List<ImageType> findAllByCategory(
            @Param("categoryId") Long categoryId,
            @Param("limit") int limit, @Param("offset") long offset
    );

    @Query(nativeQuery = true,
            value = " SELECT count(*) From image_type" +
                    " JOIN (" +
                    " SELECT id FROM image_type i" +
                    " WHERE i.category_id = :categoryId" +
                    " ORDER BY i.id DESC" +
                    " LIMIT :limit OFFSET 0" +
                    ") as image_type_ids" +
                    " ON image_type.id = image_type_ids.id"
    )
    int countByPageInfo(
            @Param("categoryId") Long categoryId,
            @Param("limit") int limit
    );

    @Query("SELECT count(i) FROM ImageType i" +
            " WHERE i.category.id = :categoryId")
    int countFullScan(@Param("categoryId") Long categoryId);

    @Query("SELECT i FROM ImageType i" +
            " LEFT JOIN FETCH i.imageUrls" +
            " WHERE i.id = :imageTypeId")
    Optional<ImageType> findByIdFetchImageUrls(@Param("imageTypeId") Long imageTypeId);

    void deleteByCategoryId(Long categoryId);
}
