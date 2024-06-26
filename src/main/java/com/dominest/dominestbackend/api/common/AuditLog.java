package com.dominest.dominestbackend.api.common;

import com.dominest.dominestbackend.domain.common.jpa.BaseEntity;
import com.dominest.dominestbackend.global.util.PrincipalParser;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class AuditLog {
    String createdBy;
    String lastModifiedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    LocalDateTime createTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    LocalDateTime lastModifiedTime;

    public static AuditLog from(BaseEntity baseEntity){
        return AuditLog.builder()
                .createdBy(PrincipalParser.toName(baseEntity.getCreatedBy()))
                .lastModifiedBy(PrincipalParser.toName(baseEntity.getLastModifiedBy()))
                .createTime(baseEntity.getCreateTime())
                .lastModifiedTime(baseEntity.getLastModifiedTime())
                .build();
    }
}
