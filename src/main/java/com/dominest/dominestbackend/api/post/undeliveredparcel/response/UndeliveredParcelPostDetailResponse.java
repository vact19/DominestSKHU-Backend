package com.dominest.dominestbackend.api.post.undeliveredparcel.response;

import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.entity.UndeliveredParcelPost;
import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.component.entity.UndeliveredParcel;
import com.dominest.dominestbackend.global.util.PrincipalParser;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class UndeliveredParcelPostDetailResponse {
    UndeliveredParcelPostDto postDetail;

    public static UndeliveredParcelPostDetailResponse from(UndeliveredParcelPost post) {
        UndeliveredParcelPostDto postDto = UndeliveredParcelPostDto.from(post);
        return new UndeliveredParcelPostDetailResponse(postDto);
    }

    @Getter
    @Builder
    private static class UndeliveredParcelPostDto {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime createTime;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime lastModifiedTime;
        String title;
        String lastModifiedBy;
        List<UndeliveredParcelDto> undelivParcels;

        static UndeliveredParcelPostDto from(UndeliveredParcelPost post) {
            List<UndeliveredParcelDto> parcelDtos = UndeliveredParcelDto.from(post.getUndelivParcels());

            return UndeliveredParcelPostDto.builder()
                    .createTime(post.getCreateTime())
                    .lastModifiedTime(post.getLastModifiedTime())
                    .title(post.getTitle())
                    .lastModifiedBy(PrincipalParser.toName(post.getLastModifiedBy()))
                    .undelivParcels(parcelDtos)
                    .build();
        }
    }

    @Getter
    @Builder
    private static class UndeliveredParcelDto {
        Long id;
        String recipientName;
        String recipientPhoneNum;
        String instruction;
        UndeliveredParcel.ProcessState processState;

        String lastModifiedBy;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime lastModifiedTime;

        static UndeliveredParcelDto from(UndeliveredParcel undelivParcel) {
            return UndeliveredParcelDto.builder()
                    .id(undelivParcel.getId())
                    .recipientName(undelivParcel.getRecipientName())
                    .recipientPhoneNum(undelivParcel.getRecipientPhoneNum())
                    .instruction(undelivParcel.getInstruction())
                    .processState(undelivParcel.getProcessState())
                    .lastModifiedBy(PrincipalParser.toName(undelivParcel.getLastModifiedBy()))
                    .lastModifiedTime(undelivParcel.getLastModifiedTime())
                    .build();
        }
        static List<UndeliveredParcelDto> from(List<UndeliveredParcel> undelivParcels) {
            return undelivParcels.stream()
                    .sorted(Comparator.comparing(UndeliveredParcel::getId).reversed())
                    .map(UndeliveredParcelDto::from)
                    .collect(Collectors.toList());
        }
    }
}
