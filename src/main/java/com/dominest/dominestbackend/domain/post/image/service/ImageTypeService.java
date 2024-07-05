package com.dominest.dominestbackend.domain.post.image.service;

import com.dominest.dominestbackend.api.common.PageInfo;
import com.dominest.dominestbackend.api.post.image.request.SaveImageTypeRequest;
import com.dominest.dominestbackend.api.post.image.response.ImageTypeListResponse;
import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.post.common.RecentPost;
import com.dominest.dominestbackend.domain.post.common.RecentPostService;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import com.dominest.dominestbackend.domain.post.image.entity.ImageType;
import com.dominest.dominestbackend.domain.post.image.repository.ImageTypeRepository;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.domain.user.repository.UserRepository;
import com.dominest.dominestbackend.domain.user.service.UserService;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import com.dominest.dominestbackend.global.util.FileManager;
import com.dominest.dominestbackend.global.util.PagingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final UserRepository userRepository;

    @Transactional
    public Long save(SaveImageTypeRequest request
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
        recentPostService.save(recentPost);

        return saved.getId();
    }

    public ImageType getById(Long imageTypeId) {
        return imageTypeRepository.findByIdFetchWriterAndImageUrls(imageTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.IMAGE_TYPE, imageTypeId));
    }

    public ImageTypeListResponse getPage(Category category, int page, int pageSize, int pageDisplayLimit) {
        final int PAGE_COUNT_SCAN_THRESHOLD = 10000; // 10000페이지 이상은 count full scan 이 빠를 가능성이 큼
        List<ImageType> imageTypes = imageTypeRepository.findAllByCategory(category.getId(), pageSize, (long) page * pageSize);

        int count;
        if (page < PAGE_COUNT_SCAN_THRESHOLD) {
            count = imageTypeRepository.countByPageInfo(category.getId(), PagingUtil.getPageGroupLimit(page, pageDisplayLimit, pageSize));
        } else {
            count = imageTypeRepository.countFullScan(category.getId());
        }

        PageInfo pageInfo = PageInfo.from(pageSize, page, imageTypes.size(), count);

        return ImageTypeListResponse.from(imageTypes, category, pageInfo);
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

    @Transactional
    public void dummyInsert(int batchSize) {
        List<User> users = userRepository.findAll();
        Category category = categoryService.getById(5L);

        List<ImageType> imageTypesToSave = new ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            User user = users.get(i % users.size());
            ImageType imageType = ImageType.builder()
                    .title("dummy" + i)
                    .writer(user)
                    .category(category)
                    .build();
            imageTypesToSave.add(imageType);
        }
        imageTypesToSave.stream().parallel().forEach(imageTypeRepository::save);
    }
}
