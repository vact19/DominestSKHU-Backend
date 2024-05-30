package com.dominest.dominestbackend.domain.post.undeliveredparcelpost.entity;

import com.dominest.dominestbackend.domain.post.common.Post;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.component.entity.UndeliveredParcel;
import com.dominest.dominestbackend.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 장기미수령 택배 관리대장 게시글
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UndeliveredParcelPost extends Post {
    @OneToMany(targetEntity = UndeliveredParcel.class, mappedBy = "post"
            , fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    List<UndeliveredParcel> undelivParcels = new ArrayList<>();

    @Builder
    private UndeliveredParcelPost(User writer, Category category) {
        // currentDate는 "yyyy-MM-dd 장기미수령 택배" 형식의 문자열이다.
        super(createDefaultTitle(), writer, category);
    }

    private static String createDefaultTitle() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = LocalDateTime.now().format(formatter);
        return formattedDate + " 장기미수령 택배";
    }
}
