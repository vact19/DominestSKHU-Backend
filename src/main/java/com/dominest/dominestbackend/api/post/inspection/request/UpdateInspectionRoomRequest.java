package com.dominest.dominestbackend.api.post.inspection.request;

import com.dominest.dominestbackend.domain.post.inspection.floor.room.entity.InspectionRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdateInspectionRoomRequest {
    /**
     * 어떤 체크박스를 클릭했든지 하나의 API로 대응하기 위해
     * 아래의 값들은 NULL을 모두 허용한다.
     * 클라이언트 단에서 하나의 필드만 보낼 것이며,
     * 서버에서는 NULL을 제외한 해당 필드만 업데이트할 것이다.
     */
    Boolean indoor;
    Boolean leavedTrash;
    Boolean toilet;
    Boolean shower;
    Boolean prohibitedItem;
    InspectionRoom.PassState passState; // 미통과 1차통과 2차통과...
    String etc;
}
