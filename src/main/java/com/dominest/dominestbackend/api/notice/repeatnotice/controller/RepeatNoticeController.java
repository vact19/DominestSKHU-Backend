package com.dominest.dominestbackend.api.notice.repeatnotice.controller;

import com.dominest.dominestbackend.api.common.ResponseTemplate;
import com.dominest.dominestbackend.api.notice.repeatnotice.request.RepeatNoticeSaveRequest;
import com.dominest.dominestbackend.api.notice.repeatnotice.response.RepeatNoticeSaveResponse;
import com.dominest.dominestbackend.domain.notice.repeatnotice.service.RepeatNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RepeatNoticeController {

    private final RepeatNoticeService repeatNoticeService;

    @PostMapping("/repeat-notice")
    public ResponseTemplate<List<RepeatNoticeSaveResponse>> createDayNotices(@RequestBody List<RepeatNoticeSaveRequest> requests) {
        List<RepeatNoticeSaveResponse> repeatNotices = repeatNoticeService.createDayNotices(requests);

        return new ResponseTemplate<>(HttpStatus.OK
                , "알림이 성공적으로 생성되었습니다.", repeatNotices);
    }

    @GetMapping("/repeat-notice")
    public ResponseTemplate<List<String>> getDayNoticeContent(@RequestParam int dayOfWeek, @RequestParam String time) {
        LocalTime requestTime = LocalTime.parse(time);
        List<String> contents = repeatNoticeService.getDayNoticeContent(dayOfWeek, requestTime);

        return new ResponseTemplate<>(HttpStatus.OK, "알림을 성공적으로 불러왔습니다.", contents);
    }
}
