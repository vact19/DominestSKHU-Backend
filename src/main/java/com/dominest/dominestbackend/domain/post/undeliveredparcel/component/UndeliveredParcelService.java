package com.dominest.dominestbackend.domain.post.undeliveredparcel.component;

import com.dominest.dominestbackend.api.post.undeliveredparcel.request.CreateUndelivParcelRequest;
import com.dominest.dominestbackend.api.post.undeliveredparcel.request.UpdateUndelivParcelDtoRequest;
import com.dominest.dominestbackend.domain.post.undeliveredparcel.UndeliveredParcelPost;
import com.dominest.dominestbackend.domain.post.undeliveredparcel.UndeliveredParcelPostService;
import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.util.EntityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**관리대장 게시글 내부의 관리물품 데이터를 처리*/
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UndeliveredParcelService {
    private final UndeliveredParcelRepository undeliveredParcelRepository;
    private final UndeliveredParcelPostService undeliveredParcelPostService;

    /**관리물품 등록
     * 관리물품 저장 전에 관리대장 게시글을 찾아서 영속화시켜야 함. */
    @Transactional
    public Long create(Long undelivParcelPostId,
                       CreateUndelivParcelRequest request) {
        // 관리대장 게시글 찾기
        UndeliveredParcelPost undelivParcelPost = undeliveredParcelPostService.getById(undelivParcelPostId);

        // 관리물품 객체 생성 후 저장
        UndeliveredParcel undelivParcel = UndeliveredParcel.builder()
                .recipientName(request.getRecipientName())
                .recipientPhoneNum(request.getRecipientPhoneNum())
                .instruction(request.getInstruction())
                .processState(request.getProcessState())
                .post(undelivParcelPost)
                .build();

        return undeliveredParcelRepository.save(undelivParcel).getId();
    }

    @Transactional
    public long update(Long undelivParcelId, UpdateUndelivParcelDtoRequest request) {
        UndeliveredParcel undelivParcel = getById(undelivParcelId);
        undelivParcel.updateValues(
                request.getRecipientName()
                , request.getRecipientPhoneNum()
                , request.getInstruction()
                , request.getProcessState()
        );

        return undelivParcel.getId();
    }

    public UndeliveredParcel getById(Long undelivParcelId) {
        return EntityUtil.mustNotNull(undeliveredParcelRepository.findById(undelivParcelId)
                , ErrorCode.UNDELIVERED_PARCEL_NOT_FOUND);
    }

    @Transactional
    public long delete(Long undelivParcelId) {
        UndeliveredParcel undelivParcel = getById(undelivParcelId);
        undeliveredParcelRepository.delete(undelivParcel);
        return undelivParcel.getId();
    }
}



















