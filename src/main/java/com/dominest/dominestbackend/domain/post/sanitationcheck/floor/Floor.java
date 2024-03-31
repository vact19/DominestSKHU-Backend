package com.dominest.dominestbackend.domain.post.sanitationcheck.floor;

import com.dominest.dominestbackend.domain.common.BaseEntity;
import com.dominest.dominestbackend.domain.post.sanitationcheck.SanitationCheckPost;
import com.dominest.dominestbackend.domain.post.sanitationcheck.floor.checkedroom.CheckedRoom;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Floor extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "floor", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<CheckedRoom> checkedRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sanitation_check_post_id", nullable = false)
    private SanitationCheckPost sanitationCheckPost;

    @Column(nullable = false)
    private int floorNumber;

    @Builder
    private Floor(int floorNumber, SanitationCheckPost sanitationCheckPost) {
        if (! (floorNumber >= 2 && floorNumber <= 10)) {
            throw new IllegalArgumentException("층수는 2 이상 10 이하의 값이어야 합니다.");
        }
        this.floorNumber = floorNumber;
        this.sanitationCheckPost = sanitationCheckPost;
    }
}














