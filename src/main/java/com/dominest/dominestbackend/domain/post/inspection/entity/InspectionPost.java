package com.dominest.dominestbackend.domain.post.inspection.entity;

import com.dominest.dominestbackend.domain.post.common.Post;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.inspection.floor.entity.InspectionFloor;
import com.dominest.dominestbackend.domain.resident.entity.component.ResidenceSemester;
import com.dominest.dominestbackend.domain.user.entity.User;
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
        super(createDefaultTitle(), writer, category);
        this.residenceSemester = residenceSemester;
    }

    private static String createDefaultTitle() {
        LocalDateTime now = LocalDateTime.now();
        return now.getYear() +
                "년 " +
                now.getMonthValue() +
                "월 방역호실점검";
    }
}
