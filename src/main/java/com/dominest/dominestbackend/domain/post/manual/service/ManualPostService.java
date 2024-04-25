package com.dominest.dominestbackend.domain.post.manual.service;

import com.dominest.dominestbackend.api.post.manual.dto.CreateManualPostDto;
import com.dominest.dominestbackend.api.post.manual.dto.UpdateManualPostDto;
import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import com.dominest.dominestbackend.domain.post.manual.repository.ManualPostRepository;
import com.dominest.dominestbackend.domain.post.manual.entity.ManualPost;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.domain.user.service.UserService;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import com.dominest.dominestbackend.global.util.FileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

import static com.dominest.dominestbackend.global.util.FileManager.FilePrefix.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ManualPostService {
    private final CategoryService categoryService;
    private final UserService userService;
    private final ManualPostRepository manualPostRepository;
    private final FileManager fileManager;

    private static final String FILE_PATH_PREFIX = "manual/";
    private static final String FILE_PATH_SUFFIX = "/";

    @Transactional
    public Long create(Long categoryId, CreateManualPostDto.Req reqDto, String email) {
        Category category = categoryService.validateCategoryType(categoryId, Type.MANUAL);
        User user = userService.getUserByEmail(email);
        ManualPost manualPost = ManualPost.builder().
                title(reqDto.getTitle()).
                writer(user).
                category(category).
                htmlContent(reqDto.getHtmlContent()).
                build();

        Long manualPostId = manualPostRepository.save(manualPost).getId();
        saveFile(reqDto.getAttachFiles(), reqDto.getImageFiles(), reqDto.getVideoFiles(), manualPost, manualPostId);

        return manualPostId;
    }

    private void saveFile(Set<MultipartFile>attachFiles, Set<MultipartFile> imageFiles,
                          Set<MultipartFile> videoFiles, ManualPost manualPost, Long manualPostId) {

        String subPath = FILE_PATH_PREFIX +manualPostId+ FILE_PATH_SUFFIX;
        Set<String> savedAttachUrls = fileManager.save(FileManager.FilePrefix.ATTACH_TYPE, subPath, attachFiles);
        Set<String> savedImgUrls = fileManager.save(FileManager.FilePrefix.IMAGE_TYPE, subPath, imageFiles);
        Set<String> savedVideoUrls = fileManager.save(FileManager.FilePrefix.VIDEO_TYPE, subPath, videoFiles);
        manualPost.setAttachmentNames(savedAttachUrls, savedImgUrls, savedVideoUrls);
    }

    private void deleteFile(Set<String> toDeleteAttachFileUrls, Set<String> toDeleteImageFileUrls,
                            Set<String> toDeleteVideoFileUrls) {
        if(toDeleteImageFileUrls!= null)
            toDeleteImageFileUrls.forEach(fileManager::deleteFile);
        if(toDeleteVideoFileUrls != null)
            toDeleteVideoFileUrls.forEach(fileManager::deleteFile);
        if(toDeleteAttachFileUrls != null)
            toDeleteAttachFileUrls.forEach(fileManager::deleteFile);
    }

    public Page<ManualPost> getPage(Long categoryId, Pageable pageable) {
        // 카테고리 내 게시글이 1건도 없는 경우도 있으므로, 게시글과 함께 카테고리를 Join해서 데이터를 찾아오지 않는다.
        return manualPostRepository.findAllByCategory(categoryId, pageable);
    }

    public ManualPost getById(Long id) {
        return manualPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.MANUAL_POST, id));
    }

    public ManualPost getByIdIncludeAllColumn(Long manualPostId) {
        return manualPostRepository.findManualPostIncludeAllColumn(manualPostId)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.MANUAL_POST, manualPostId));
    }

    @Transactional
    public long delete(Long manualPostId) {
        ManualPost post = getById(manualPostId);
        manualPostRepository.delete(post);
        String folderPath = FILE_PATH_PREFIX +manualPostId+ FILE_PATH_SUFFIX;
        fileManager.deleteFolder(folderPath);
        return post.getId();
    }

    @Transactional
    public long update(Long manualPostId, UpdateManualPostDto.Req reqDto) {
        ManualPost manualPost = getById(manualPostId);

        //게시글 업데이트
        Optional.ofNullable(reqDto.getTitle())
                .ifPresent(manualPost::setTitle);

        Optional.ofNullable(reqDto.getHtmlContent())
                .ifPresent(manualPost::setHtmlContent);

        String subPath = FILE_PATH_PREFIX +manualPostId+ FILE_PATH_SUFFIX;

        Optional.ofNullable(reqDto.getAttachFiles())
                .ifPresent(attachFiles -> {
                    Set<String> savedAttachUrls = fileManager.save(ATTACH_TYPE, subPath, attachFiles);
                    manualPost.addAttachmentUrls(savedAttachUrls);
                });

        Optional.ofNullable(reqDto.getImageFiles())
                .ifPresent(imageFiles -> {
                    Set<String> savedImageUrls = fileManager.save(IMAGE_TYPE, subPath, imageFiles);
                    manualPost.addImageUrls(savedImageUrls);
                });

        Optional.ofNullable(reqDto.getVideoFiles())
                .ifPresent(videoFiles -> {
                    Set<String> savedVideoUrls = fileManager.save(VIDEO_TYPE, subPath, videoFiles);
                    manualPost.addVideoUrls(savedVideoUrls);
                });

        //수정으로 인해 필요 없어진 파일들 삭제
        Set<String> toDeleteAttachmentUrls = reqDto.getToDeleteAttachUrls();
        Set<String> toDeleteImageUrls = reqDto.getToDeleteImageUrls();
        Set<String> toDeleteVideoUrls = reqDto.getToDeleteVideoUrls();

        deleteFile(toDeleteAttachmentUrls, toDeleteImageUrls, toDeleteVideoUrls);
        manualPost.deleteUrls(toDeleteAttachmentUrls , toDeleteImageUrls, toDeleteVideoUrls);

        return manualPostRepository.save(manualPost).getId();
    }
}
