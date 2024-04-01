package com.dominest.dominestbackend.domain.post.inspection;

import com.dominest.dominestbackend.domain.post.common.Post;
import com.dominest.dominestbackend.domain.post.component.category.Category;
import com.dominest.dominestbackend.domain.post.inspection.floor.InspectionFloor;
import com.dominest.dominestbackend.domain.resident.component.ResidenceSemester;
import com.dominest.dominestbackend.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class InspectionPost extends Post {
    @OneToMany(mappedBy = "inspectionPost"
            , fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<InspectionFloor> inspectionFloors;

    @Enumerated(EnumType.STRING)
    private ResidenceSemester residenceSemester;

    @Builder
    private InspectionPost(User writer, Category category
            , ResidenceSemester residenceSemester
    ) {
        super(createTitle(), writer, category);
        this.residenceSemester = residenceSemester;
    }
    // 객체의 context를 전혀 반영하지 않으므로 static
    private static String createTitle() {
        // 원하는 형식의 문자열로 변환
        LocalDateTime now = LocalDateTime.now();
        return now.getYear() +
                "년 " +
                now.getMonthValue() +
                "월 방역호실점검";
    }
}
