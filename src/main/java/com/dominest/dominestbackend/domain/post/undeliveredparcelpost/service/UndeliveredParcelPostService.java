package com.dominest.dominestbackend.domain.post.undeliveredparcelpost.service;

import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.post.common.RecentPost;
import com.dominest.dominestbackend.domain.post.common.RecentPostService;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.entity.UndeliveredParcelPost;
import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.repository.UndeliveredParcelPostRepository;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.domain.user.repository.UserRepository;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UndeliveredParcelPostService {
    private final UndeliveredParcelPostRepository undelivParcelPostRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final RecentPostService recentPostService;

    @Transactional
    public Long save(Long categoryId, String email) {
        // Undeli...의 연관 객체인 category, user 찾기
        User user = userRepository.getByEmail(email);
        // Undeli...의 연관 객체인 category 찾기
        Category category = categoryService.validateCategoryType(categoryId, Type.UNDELIVERED_PARCEL_REGISTER);

        // Undeli... 객체 생성 후 저장
        UndeliveredParcelPost unDeliParcelPost = UndeliveredParcelPost.builder()
                .category(category)
                .writer(user)
                .build();

        UndeliveredParcelPost post = undelivParcelPostRepository.save(unDeliParcelPost);

        RecentPost recentPost = RecentPost.builder()
                .title(post.getTitle())
                .categoryLink(post.getCategory().getPostsLink())
                .categoryType(post.getCategory().getType())
                .link("/posts/undelivered-parcel/" + post.getId())
                .build();
        recentPostService.save(recentPost);

        return post.getId();
    }

    @Transactional
    public void renameTitle(Long id, String title) {
        UndeliveredParcelPost post = getById(id);
        post.setTitle(title);
    }

    public UndeliveredParcelPost getById(Long id) {
        return undelivParcelPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.UNDELIVERED_PARCEL_POST, id));
    }

    public UndeliveredParcelPost getByIdFetchParcels(Long id) {
        return undelivParcelPostRepository.findByIdFetchParcels(id)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.UNDELIVERED_PARCEL_POST, id));
    }

    @Transactional
    public long delete(Long undelivParcelPostId) {
        UndeliveredParcelPost post = getById(undelivParcelPostId);
        undelivParcelPostRepository.delete(post);
        return post.getId();
    }

    public Page<UndeliveredParcelPost> getPage(Long categoryId, Pageable pageable) {
        // 카테고리 내 게시글이 1건도 없는 경우도 있으므로, 게시글과 함께 카테고리를 Join해서 데이터를 찾아오지 않는다.
        return undelivParcelPostRepository.findAllByCategory(categoryId, pageable);
    }
}
