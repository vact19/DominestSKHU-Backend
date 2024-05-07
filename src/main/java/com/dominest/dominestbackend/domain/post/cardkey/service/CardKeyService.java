package com.dominest.dominestbackend.domain.post.cardkey.service;

import com.dominest.dominestbackend.api.post.cardkey.request.CreateCardKeyRequest;
import com.dominest.dominestbackend.api.post.cardkey.request.UpdateCardKeyRequest;
import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.post.cardkey.entity.CardKey;
import com.dominest.dominestbackend.domain.post.cardkey.repository.CardKeyRepository;
import com.dominest.dominestbackend.domain.post.common.RecentPost;
import com.dominest.dominestbackend.domain.post.common.RecentPostService;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.domain.user.service.UserService;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CardKeyService {
    private final CardKeyRepository cardKeyRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final RecentPostService recentPostService;

    @Transactional
    public long create(CreateCardKeyRequest request, Long categoryId, String email) {
        // CardKey 연관 객체인 category, user 찾기
        User user = userService.getUserByEmail(email);
        Category category = categoryService.validateCategoryType(categoryId, Type.CARD_KEY);

        CardKey cardKey = request.toEntity(user, category);

        CardKey key = cardKeyRepository.save(cardKey);

        RecentPost recentPost = RecentPost.builder()
                .title(key.getRoomNo() + "호 카드키 기록")
                .categoryLink(key.getCategory().getPostsLink())
                .categoryType(key.getCategory().getType())
                .link(null)
                .build();
        recentPostService.create(recentPost);

        return key.getId();
    }

    @Transactional
    public long update(Long cardKeyId, UpdateCardKeyRequest request) {
        CardKey cardKey = getById(cardKeyId);

        cardKey.updateValues(
                request.getIssuedDate()
                , request.getRoomNo()
                , request.getName()
                , request.getDateOfBirth()
                , request.getReIssueCnt()
                , request.getEtc()
        );
        return cardKey.getId();
    }

    public CardKey getById(Long id) {
        return cardKeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.CARD_KEY, id));
    }

    @Transactional
    public long delete(Long id) {
        CardKey cardKey = getById(id);
        cardKeyRepository.delete(cardKey);

        return cardKey.getId();
    }

    public Page<CardKey> getPage(Long id, Pageable pageable, String name) {
        if (StringUtils.hasText(name)) {
            return cardKeyRepository.findAllByCategoryIdAndNameStartsWith(id, name, pageable);
        }
        return cardKeyRepository.findAllByCategoryId(id, pageable);
    }
}
