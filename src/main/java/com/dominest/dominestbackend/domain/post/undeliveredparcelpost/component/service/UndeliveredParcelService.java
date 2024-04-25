package com.dominest.dominestbackend.domain.post.undeliveredparcelpost.component.service;

import com.dominest.dominestbackend.api.post.undeliveredparcel.request.CreateUndelivParcelRequest;
import com.dominest.dominestbackend.api.post.undeliveredparcel.request.UpdateUndelivParcelDtoRequest;
import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.entity.UndeliveredParcelPost;
import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.service.UndeliveredParcelPostService;
import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.component.entity.UndeliveredParcel;
import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.component.repository.UndeliveredParcelRepository;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
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

    public UndeliveredParcel getById(Long id) {
        return undeliveredParcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.UNDELIVERED_PARCEL, id));
    }

    @Transactional
    public long delete(Long undelivParcelId) {
        UndeliveredParcel undelivParcel = getById(undelivParcelId);
        undeliveredParcelRepository.delete(undelivParcel);
        return undelivParcel.getId();
    }
}
