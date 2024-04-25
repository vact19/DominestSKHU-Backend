package com.dominest.dominestbackend.api.post.cardkey.response;

import com.dominest.dominestbackend.api.common.AuditLog;
import com.dominest.dominestbackend.api.common.CategoryResponse;
import com.dominest.dominestbackend.api.common.PageInfo;
import com.dominest.dominestbackend.domain.post.cardkey.entity.CardKey;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class CardKeyListResponse {
    CategoryResponse category;
    PageInfo page;

    List<CardKeyDto> cardKeys;

    public static CardKeyListResponse from(Page<CardKey> cardKeyPage, Category category) {
        CategoryResponse categoryResponse = CategoryResponse.from(category);
        PageInfo pageInfo = PageInfo.from(cardKeyPage);

        List<CardKeyDto> complaints = CardKeyDto.from(cardKeyPage);
        return new CardKeyListResponse(categoryResponse, pageInfo, complaints);
    }

    @Builder
    @Getter
    static class CardKeyDto {
        long id;
        LocalDate issuedDate; // 카드키 발급일자
        String roomNo; // N호실
        String name; // 이름
        LocalDate dateOfBirth; // 생년월일
        Integer reIssueCnt; // 재발급 횟수

        String etc;     // 비고
        AuditLog auditLog; // 여기에 '작성자' 있음.

        static CardKeyDto from(CardKey cardKey){
            return CardKeyDto.builder()
                    .id(cardKey.getId())
                    .issuedDate(cardKey.getIssuedDate())
                    .roomNo(cardKey.getRoomNo())
                    .name(cardKey.getName())
                    .dateOfBirth(cardKey.getDateOfBirth())
                    .reIssueCnt(cardKey.getReIssueCnt())
                    .etc(cardKey.getEtc())
                    .auditLog(AuditLog.from(cardKey))
                    .build();
        }

        static List<CardKeyDto> from(Page<CardKey> cardKeyPage){
            return cardKeyPage
                    .map(CardKeyDto::from)
                    .toList();
        }
    }
}
