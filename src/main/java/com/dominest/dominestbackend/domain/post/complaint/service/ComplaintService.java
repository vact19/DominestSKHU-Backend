package com.dominest.dominestbackend.domain.post.complaint.service;

import com.dominest.dominestbackend.api.post.complaint.request.CreateComplaintRequest;
import com.dominest.dominestbackend.api.post.complaint.request.UpdateComplaintRequest;
import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.post.common.RecentPost;
import com.dominest.dominestbackend.domain.post.common.RecentPostService;
import com.dominest.dominestbackend.domain.post.complaint.repository.ComplaintRepository;
import com.dominest.dominestbackend.domain.post.complaint.entity.Complaint;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.domain.user.repository.UserRepository;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ComplaintService {
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final RecentPostService recentPostService;

    @Transactional
    public long save(CreateComplaintRequest request, Long categoryId, String email) {
        // Complaint 연관 객체인 category, user 찾기
        User user = userRepository.getByEmail(email);
        // Complaint 연관 객체인 category 찾기
        Category category = categoryService.validateCategoryType(categoryId, Type.COMPLAINT);

        Complaint complaint = request.toEntity(user, category);


        Complaint compl = complaintRepository.save(complaint);
        RecentPost recentPost = RecentPost.builder()
                .title(compl.getRoomNo() + "호 민원 기록")
                .categoryLink(compl.getCategory().getPostsLink())
                .categoryType(compl.getCategory().getType())
                .link(null)
                .build();
        recentPostService.save(recentPost);

        // Complaint 객체 생성 후 저장
        return compl.getId();
    }

    @Transactional
    public long update(Long complaintId, UpdateComplaintRequest request) {
        Complaint complaint = getById(complaintId);

        complaint.updateValues(
                request.getName()
                , request.getRoomNo()
                , request.getComplaintCause()
                , request.getComplaintResolution()
                , request.getProcessState()
                , request.getDate()
        );
        return complaint.getId();
    }

    public Complaint getById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.COMPLAINT, id));
    }

    @Transactional
    public long delete(Long id) {
        Complaint complaint = getById(id);
        complaintRepository.delete(complaint);
        return complaint.getId();
    }

    public Page<Complaint> getPage(Long categoryId, Pageable pageable, String complaintSearch, String roomNoSearch) {
        if (StringUtils.hasText(roomNoSearch)) {
            return complaintRepository.findAllByCategoryIdAndRoomNo(categoryId, roomNoSearch, pageable);
        }

        if (StringUtils.hasText(complaintSearch)) {
            return complaintRepository.findAllByCategoryIdSearch(categoryId, complaintSearch + "*", pageable);
        }
        return complaintRepository.findPageAllByCategoryId(categoryId, pageable);
    }
}
