package com.dominest.dominestbackend.domain.post.undeliveredparcelpost.component.repository;

import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.component.entity.UndeliveredParcel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UndeliveredParcelRepository extends JpaRepository<UndeliveredParcel, Long> {
}
