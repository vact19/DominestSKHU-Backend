package com.dominest.dominestbackend.domain.post.inspection.floor.entity;

import com.dominest.dominestbackend.domain.common.jpa.BaseEntity;
import com.dominest.dominestbackend.domain.post.inspection.entity.InspectionPost;
import com.dominest.dominestbackend.domain.post.inspection.floor.room.entity.InspectionRoom;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class InspectionFloor extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "inspectionFloor", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<InspectionRoom> inspectionRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_post_id", nullable = false)
    private InspectionPost inspectionPost;

    @Column(nullable = false)
    private int floorNumber;

    @Builder
    private InspectionFloor(int floorNumber, InspectionPost inspectionPost) {
        if (! (floorNumber >= 2 && floorNumber <= 10)) {
            throw new IllegalArgumentException("층수는 2 이상 10 이하의 값이어야 합니다.");
        }
        this.floorNumber = floorNumber;
        this.inspectionPost = inspectionPost;
    }
}
