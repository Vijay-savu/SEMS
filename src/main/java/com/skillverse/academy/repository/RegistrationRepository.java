package com.skillverse.academy.repository;

import com.skillverse.academy.model.Registration;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RegistrationRepository extends MongoRepository<Registration, String> {

    List<Registration> findByAttendeeEmailIgnoreCaseOrderByRegisteredAtDesc(String attendeeEmail);

    List<Registration> findByEventIdOrderByRegisteredAtDesc(String eventId);

    void deleteByEventId(String eventId);
}
