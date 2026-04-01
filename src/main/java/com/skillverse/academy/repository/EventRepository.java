package com.skillverse.academy.repository;

import com.skillverse.academy.model.Event;
import com.skillverse.academy.model.EventType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByPublishedTrueAndEventDateTimeAfterOrderByEventDateTimeAsc(LocalDateTime now);

    List<Event> findByPublishedTrueAndDepartmentContainingIgnoreCaseAndEventDateTimeAfterOrderByEventDateTimeAsc(
            String department,
            LocalDateTime now
    );

    List<Event> findByPublishedTrueAndTypeAndEventDateTimeAfterOrderByEventDateTimeAsc(
            EventType type,
            LocalDateTime now
    );

    List<Event> findByPublishedTrueAndDepartmentContainingIgnoreCaseAndTypeAndEventDateTimeAfterOrderByEventDateTimeAsc(
            String department,
            EventType type,
            LocalDateTime now
    );

    long countByPublishedTrue();

    @Query("select count(distinct e.department) from Event e")
    long countDistinctDepartments();
}
