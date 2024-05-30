package com.dominest.dominestbackend.api.post.complaint.request;

import com.dominest.dominestbackend.domain.post.complaint.entity.Complaint;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class CreateComplaintRequest {
    @NotBlank(message = "민원인 이름을 입력해주세요.")
    String name;    //민원인 이름
    @NotBlank(message = "기숙사 방 번호를 입력해주세요.")
    String roomNo; // N호실

    // 바인딩 실패 혹은 NULL 삽입 시 empty String으로 대체
    String complaintCause = ""; // 민원내역.
    // 바인딩 실패 혹은 NULL 삽입 시 empty String으로 대체
    String complaintResolution = ""; // 민원처리내역.

    @NotNull(message = "민원처리상태를 입력해주세요.")
    Complaint.ProcessState processState;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "민원접수일자를 입력해주세요.")
    LocalDate date; // 민원접수일자

    public Complaint toEntity(User user, Category category) {
        return Complaint.builder()
                .name(name)
                .roomNo(roomNo)
                .complaintCause(complaintCause)
                .complaintResolution(complaintResolution)
                .processState(processState)
                .date(date)
                .writer(user)
                .category(category)
                .build();
    }
}
