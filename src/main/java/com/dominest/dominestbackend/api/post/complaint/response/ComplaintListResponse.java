package com.dominest.dominestbackend.api.post.complaint.response;

import com.dominest.dominestbackend.api.common.AuditLog;
import com.dominest.dominestbackend.api.common.CategoryResponse;
import com.dominest.dominestbackend.api.common.PageInfo;
import com.dominest.dominestbackend.domain.post.complaint.entity.Complaint;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
public class ComplaintListResponse {
    CategoryResponse category;
    PageInfo page;

    List<ComplaintDto> complaints;

    public static ComplaintListResponse from(Page<Complaint> complaintPage, Category category) {
        CategoryResponse categoryResponse = CategoryResponse.from(category);
        PageInfo pageInfo = PageInfo.from(complaintPage);

        List<ComplaintDto> complaints = ComplaintDto.from(complaintPage);
        return new ComplaintListResponse(categoryResponse, pageInfo, complaints);
    }

    @Builder
    @Getter
    static class ComplaintDto {
        Long id;
        String name;    //민원인 이름
        LocalDate date; // 민원접수일자
        String roomNo; // 호실
        String complaintCause; // 민원내역.
        String complaintResolution; // 민원처리내역.
        Complaint.ProcessState processState; // 처리상태

        AuditLog auditLog;

        static ComplaintDto from(Complaint complaint){
            return ComplaintDto.builder()
                    .id(complaint.getId())
                    .name(complaint.getName())
                    .date(complaint.getDate())
                    .roomNo(complaint.getRoomNo())
                    .complaintCause(complaint.getComplaintCause())
                    .complaintResolution(complaint.getComplaintResolution())
                    .processState(complaint.getProcessState())
                    .auditLog(AuditLog.from(complaint))
                    .build();
        }

        static List<ComplaintDto> from(Page<Complaint> complaints){
            return complaints
                    .map(ComplaintDto::from)
                    .toList();
        }
    }
}
