package com.dominest.dominestbackend.domain.post.complaint.entity;

import com.dominest.dominestbackend.domain.common.jpa.BaseEntity;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Complaint extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;    //민원인 이름
    @Column(nullable = false)
    private String roomNo; // 호실
    @Column(nullable = false)
    private String complaintCause; // 민원내역. FtIdx 만들어야 함.
    @Column(nullable = false)
    private String complaintResolution; // 민원처리내역. FtIdx 만들어야 함.

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProcessState processState; // 처리상태

    @Column(nullable = false)
    private LocalDate date; // 민원접수일자. 사용자 요청에 의한 DB 등록시간(createTime)과 실제 접수일이 다를 수 있음.


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "writer_id")
    private User writer;    // 작성자 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "category_id")
    private Category category;

    @Builder
    private Complaint(String name, String roomNo, String complaintCause, String complaintResolution, ProcessState processState, LocalDate date, User writer, Category category) {
        this.name = name;
        this.roomNo = roomNo;
        this.complaintCause = complaintCause;
        this.complaintResolution = complaintResolution;
        this.processState = processState;
        this.date = date;
        this.writer = writer;
        this.category = category;
    }

    public void updateValues(String name, String roomNo, String complaintCause, String complaintResolution, ProcessState processState, LocalDate date) {
        this.name = name;
        this.roomNo = roomNo;
        this.complaintCause = complaintCause;
        this.complaintResolution = complaintResolution;
        this.processState = processState;
        this.date = date;
    }

    // 처리 결과는 (접수완료, 처리중, 처리완료 중 하나)
    @RequiredArgsConstructor
    public enum ProcessState {
        RECEIPT_COMPLETED("접수완료"), PROCESSING("처리중"), PROCESS_COMPLETED("처리완료");

        @JsonValue // 직렬화, 역직렬화 시 사용될 값
        public final String state;
    }
}
