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
public class UndelivParcelPostDetailResponse {
    UndelivParcelPostDto postDetail;

    public static UndelivParcelPostDetailResponse from(UndeliveredParcelPost post) {
        UndelivParcelPostDto postDto = UndelivParcelPostDto.from(post);
        return new UndelivParcelPostDetailResponse(postDto);
    }

    @Getter
    @Builder
    private static class UndelivParcelPostDto {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime createTime;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime lastModifiedTime;
        String title;
        String lastModifiedBy;
        List<UndelivParcelDto> undelivParcels;

        static UndelivParcelPostDto from(UndeliveredParcelPost post) {
            List<UndelivParcelDto> parcelDtos = UndelivParcelDto.from(post.getUndelivParcels());

            return UndelivParcelPostDto.builder()
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
    private static class UndelivParcelDto {
        Long id;
        String recipientName;
        String recipientPhoneNum;
        String instruction;
        UndeliveredParcel.ProcessState processState;

        String lastModifiedBy;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime lastModifiedTime;

        static UndelivParcelDto from(UndeliveredParcel undelivParcel) {
            return UndelivParcelDto.builder()
                    .id(undelivParcel.getId())
                    .recipientName(undelivParcel.getRecipientName())
                    .recipientPhoneNum(undelivParcel.getRecipientPhoneNum())
                    .instruction(undelivParcel.getInstruction())
                    .processState(undelivParcel.getProcessState())
                    .lastModifiedBy(PrincipalParser.toName(undelivParcel.getLastModifiedBy()))
                    .lastModifiedTime(undelivParcel.getLastModifiedTime())
                    .build();
        }
        static List<UndelivParcelDto> from(List<UndeliveredParcel> undelivParcels) {
            return undelivParcels.stream()
                    .sorted(Comparator.comparing(UndeliveredParcel::getId).reversed())
                    .map(UndelivParcelDto::from)
                    .collect(Collectors.toList());
        }
    }
}
