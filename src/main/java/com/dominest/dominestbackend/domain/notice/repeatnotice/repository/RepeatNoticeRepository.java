package com.dominest.dominestbackend.domain.notice.repeatnotice.repository;

import com.dominest.dominestbackend.domain.notice.repeatnotice.entity.RepeatNotice;
import com.dominest.dominestbackend.domain.notice.repeatschedule.entity.RepeatSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepeatNoticeRepository extends JpaRepository<RepeatNotice, Long> {

    List<RepeatNotice> findAllByRepeatSchedule(RepeatSchedule repeatSchedule);
}
