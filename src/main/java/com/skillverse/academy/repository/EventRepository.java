package com.skillverse.academy.repository;

import com.skillverse.academy.model.Event;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event, String> {

    List<Event> findByPublishedTrueAndEventDateTimeAfterOrderByEventDateTimeAsc(LocalDateTime now);

    long countByPublishedTrue();

    boolean existsByNameIgnoreCase(String name);
}
