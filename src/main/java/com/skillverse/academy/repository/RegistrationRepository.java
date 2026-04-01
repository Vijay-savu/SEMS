package com.skillverse.academy.repository;

import com.skillverse.academy.model.Registration;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    @EntityGraph(attributePaths = "event")
    List<Registration> findByAttendeeEmailIgnoreCaseOrderByRegisteredAtDesc(String attendeeEmail);

    @EntityGraph(attributePaths = "event")
    List<Registration> findByEventIdOrderByRegisteredAtDesc(Long eventId);

    @Query("select coalesce(sum(r.ticketsBooked), 0) from Registration r")
    long getTotalBookedSeats();
}
