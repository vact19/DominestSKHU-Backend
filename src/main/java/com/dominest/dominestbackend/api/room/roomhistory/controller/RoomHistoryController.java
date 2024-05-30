package com.dominest.dominestbackend.api.room.roomhistory.controller;

import com.dominest.dominestbackend.api.common.ResponseTemplate;
import com.dominest.dominestbackend.api.room.roomhistory.response.RoomHistoryListResponse;
import com.dominest.dominestbackend.domain.resident.entity.component.ResidenceSemester;
import com.dominest.dominestbackend.domain.room.entity.Room;
import com.dominest.dominestbackend.domain.room.roomhistory.entity.RoomHistory;
import com.dominest.dominestbackend.domain.room.roomhistory.service.RoomHistoryService;
import com.dominest.dominestbackend.domain.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Validated
@RequestMapping("/rooms")
@RestController
public class RoomHistoryController {
    private final RoomService roomService;
    private final RoomHistoryService roomHistoryService;

    // 지정한 방 ID의 거주기록 조회
    @GetMapping("/{id}/history")
    public ResponseTemplate<RoomHistoryListResponse> getRoomHistory(
            @PathVariable("id") long id
    ) {
        // Room ID로 찾고, Response 로 내보낸다.
        Room room = roomService.getById(id);
        List<RoomHistory> roomHistories = roomHistoryService.findByRoomId(room.getId());

        RoomHistoryListResponse rspDto = RoomHistoryListResponse.from(room, roomHistories);
        return new ResponseTemplate<>(HttpStatus.OK
                , id + "번 방의 거주기록 조회"
                , rspDto);
    }

    // 현재 사생 데이터를 방 거주기록에 반영.
    // 거주기록 관련 기능이 사생데이터 업로드 기능 이후에 만들어졌기 때문에 배포 후 최초 한 번은 실행해야 한다.
    @GetMapping("/init-history")
    public String initRoomHistory(
            @RequestParam ResidenceSemester residenceSemester
    ) {
        roomHistoryService.initRoomHistory(residenceSemester);
        return "방 거주기록 초기화 완료";
    }
}
