package com.skillverse.academy.config;

import com.skillverse.academy.model.Account;
import com.skillverse.academy.model.AccountRole;
import com.skillverse.academy.model.Event;
import com.skillverse.academy.model.EventCategory;
import com.skillverse.academy.model.EventType;
import com.skillverse.academy.repository.AccountRepository;
import com.skillverse.academy.repository.EventRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final String DEFAULT_ADMIN_EMAIL = "vijayyy@gmail.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "131605";

    @Bean
    CommandLineRunner seedAdminAccount(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (accountRepository.existsByEmailIgnoreCase(DEFAULT_ADMIN_EMAIL)) {
                return;
            }

            Account admin = new Account();
            admin.setEmail(DEFAULT_ADMIN_EMAIL);
            admin.setPasswordHash(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
            admin.setRole(AccountRole.ADMIN);
            admin.setActive(true);
            accountRepository.save(admin);
        };
    }

    @Bean
    CommandLineRunner seedEvents(EventRepository eventRepository) {
        return args -> {
            if (eventRepository.count() > 0) {
                return;
            }

            eventRepository.save(buildEvent(
                    "AI Research Colloquium",
                    "Computer Science Department",
                    EventCategory.DEPARTMENT,
                    EventType.SEMINAR,
                    "Main Auditorium",
                    "A faculty-led colloquium featuring student paper presentations, AI demos, and research mentor sessions.",
                    LocalDateTime.now().plusDays(5).withHour(10).withMinute(0),
                    new BigDecimal("99.00"),
                    140
            ));
            eventRepository.save(buildEvent(
                    "Cloud Native Bootcamp",
                    "Information Technology Department",
                    EventCategory.DEPARTMENT,
                    EventType.WORKSHOP,
                    "Lab Block B",
                    "A department workshop focused on Spring Boot services, deployment basics, and API security for final year students.",
                    LocalDateTime.now().plusDays(8).withHour(14).withMinute(30),
                    new BigDecimal("149.00"),
                    90
            ));
            eventRepository.save(buildEvent(
                    "Robotics Club Demo Day",
                    "Robotics Club",
                    EventCategory.CLUB,
                    EventType.WORKSHOP,
                    "Innovation Studio",
                    "Club members showcase line followers, drones, and embedded prototypes with beginner onboarding sessions.",
                    LocalDateTime.now().plusDays(11).withHour(15).withMinute(0),
                    new BigDecimal("49.00"),
                    120
            ));
            eventRepository.save(buildEvent(
                    "Coding Club Problem Solving Night",
                    "Coding Club",
                    EventCategory.CLUB,
                    EventType.WORKSHOP,
                    "Seminar Hall 2",
                    "A peer-led evening of contest strategies, team practice rounds, and mentor review for aspiring competitive programmers.",
                    LocalDateTime.now().plusDays(13).withHour(17).withMinute(30),
                    new BigDecimal("0.00"),
                    110
            ));
            eventRepository.save(buildEvent(
                    "TechFest Innovision Hack Arena",
                    "TechFest Core Team",
                    EventCategory.TECH_FEST,
                    EventType.HACKATHON,
                    "Innovation Hub",
                    "The flagship hack arena for the annual campus tech fest with judging tracks in AI, IoT, and civic tech.",
                    LocalDateTime.now().plusDays(15).withHour(9).withMinute(0),
                    new BigDecimal("299.00"),
                    250
            ));
            eventRepository.save(buildEvent(
                    "TechFest Expo and Startup Alley",
                    "TechFest Core Team",
                    EventCategory.TECH_FEST,
                    EventType.SEMINAR,
                    "Open Exhibition Center",
                    "An open expo featuring startup booths, project showcases, sponsor interactions, and prototype demonstrations.",
                    LocalDateTime.now().plusDays(16).withHour(11).withMinute(0),
                    new BigDecimal("79.00"),
                    300
            ));
            eventRepository.save(buildEvent(
                    "Campus Cultural Evening",
                    "Student Affairs Office",
                    EventCategory.COLLEGE,
                    EventType.CULTURAL,
                    "Open Air Theatre",
                    "A college-wide evening with performances, department showcases, and community celebrations open to all students.",
                    LocalDateTime.now().plusDays(20).withHour(16).withMinute(0),
                    new BigDecimal("99.00"),
                    220
            ));
        };
    }

    private Event buildEvent(
            String name,
            String department,
            EventCategory category,
            EventType type,
            String venue,
            String description,
            LocalDateTime dateTime,
            BigDecimal price,
            int capacity
    ) {
        Event event = new Event();
        event.setName(name);
        event.setDepartment(department);
        event.setCategory(category);
        event.setType(type);
        event.setVenue(venue);
        event.setDescription(description);
        event.setEventDateTime(dateTime);
        event.setTicketPrice(price);
        event.setCapacity(capacity);
        event.setAvailableSeats(capacity);
        event.setPublished(true);
        return event;
    }
}
