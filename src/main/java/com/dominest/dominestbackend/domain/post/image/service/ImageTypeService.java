package com.dominest.dominestbackend.domain.post.image.service;

import com.dominest.dominestbackend.api.post.image.request.SaveImageTypeRequest;
import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.post.common.RecentPost;
import com.dominest.dominestbackend.domain.post.common.RecentPostService;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import com.dominest.dominestbackend.domain.post.image.entity.ImageType;
import com.dominest.dominestbackend.domain.post.image.repository.ImageTypeRepository;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.domain.user.service.UserService;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import com.dominest.dominestbackend.global.util.FileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ImageTypeService {
    private final ImageTypeRepository imageTypeRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final FileManager fileManager;
    private final RecentPostService recentPostService;

    @Transactional
    public Long create(SaveImageTypeRequest request
                                    , Long categoryId, String uploaderEmail) {
        Category category = categoryService.getById(categoryId);
        // 이미지 게시물이 작성될 카테고리의 타입 검사
        Type.IMAGE.validateEqualTo(category.getType());

        User writer = userService.getUserByEmail(uploaderEmail);

        List<Optional<String>> savedImgUrls = fileManager.save(FileManager.FilePrefix.POST_IMAGE_TYPE, request.getPostImages());
        List<String> validImgUrls = extractValidImgUrls(savedImgUrls);
        ImageType imageType = request.toEntity(validImgUrls, writer, category);

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

        List<Optional<String>> savedImgUrls = fileManager.save(FileManager.FilePrefix.POST_IMAGE_TYPE, request.getPostImages());
        List<String> validImgUrls = extractValidImgUrls(savedImgUrls);

        imageType.setImageUrls(validImgUrls);
        return imageType.getId();
    }

    @Transactional
    public ImageType deleteById(Long imageTypeId) {
        ImageType imageType = imageTypeRepository.findByIdFetchImageUrls(imageTypeId)
                        .orElseThrow(() -> new ResourceNotFoundException(Datasource.IMAGE_TYPE, imageTypeId));
        imageTypeRepository.delete(imageType);
        return imageType;
    }

    private List<String> extractValidImgUrls(List<Optional<String>> savedImgUrls) {
        return savedImgUrls.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toUnmodifiableList());
    }
}
