package com.dominest.dominestbackend.domain.post.image;

import com.dominest.dominestbackend.api.post.image.request.SaveImageTypeRequest;
import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.post.common.RecentPost;
import com.dominest.dominestbackend.domain.post.common.RecentPostService;
import com.dominest.dominestbackend.domain.post.component.category.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import com.dominest.dominestbackend.domain.user.User;
import com.dominest.dominestbackend.domain.user.service.UserService;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import com.dominest.dominestbackend.global.util.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ImageTypeService {
    private final ImageTypeRepository imageTypeRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final FileService fileService;
    private final RecentPostService recentPostService;

    @Transactional
    public Long create(SaveImageTypeRequest request
                                    , Long categoryId, String uploaderEmail) {
        Category category = categoryService.getById(categoryId);
        // 이미지 게시물이 작성될 카테고리의 타입 검사
        Type.IMAGE.validateEqualTo(category.getType());

        User writer = userService.getUserByEmail(uploaderEmail);

        List<String> savedImgUrls = fileService.save(FileService.FilePrefix.POST_IMAGE_TYPE, request.getPostImages());
        ImageType imageType = request.toEntity(savedImgUrls, writer, category);


        ImageType saved = imageTypeRepository.save(imageType);
        RecentPost recentPost = RecentPost.builder()
                .title(saved.getTitle())
                .categoryLink(saved.getCategory().getPostsLink())
                .categoryType(saved.getCategory().getType())
                .link(saved.getCategory().getPostsLink() + "/" + saved.getId())
                .build();
        recentPostService.create(recentPost);

        return saved.getId();
    }

    public ImageType getById(Long imageTypeId) {
        return imageTypeRepository.findByIdFetchWriterAndImageUrls(imageTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.IMAGE_TYPE, imageTypeId));
    }

    public Page<ImageType> getPage(Long categoryId, Pageable pageable) {
        return imageTypeRepository.findAllByCategory(categoryId, pageable);
    }

    @Transactional
    public long update(SaveImageTypeRequest request, Long id) {
        ImageType imageType = imageTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.IMAGE_TYPE, id));

        List<String> savedImgUrls = fileService.save(FileService.FilePrefix.POST_IMAGE_TYPE, request.getPostImages());
        imageType.setImageUrls(savedImgUrls);
        return imageType.getId();
    }

    @Transactional
    public ImageType deleteById(Long imageTypeId) {
        ImageType imageType = imageTypeRepository.findByIdFetchImageUrls(imageTypeId)
                        .orElseThrow(() -> new ResourceNotFoundException(Datasource.IMAGE_TYPE, imageTypeId));
        imageTypeRepository.delete(imageType);
        return imageType;
    }
}











