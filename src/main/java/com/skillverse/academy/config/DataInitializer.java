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
            seedEvent(eventRepository, buildEvent(
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
            seedEvent(eventRepository, buildEvent(
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
            seedEvent(eventRepository, buildEvent(
                    "Cybersecurity Awareness Session",
                    "Electronics and Communication Department",
                    EventCategory.DEPARTMENT,
                    EventType.SEMINAR,
                    "ECE Smart Classroom",
                    "An industry-aligned session on cyber hygiene, phishing awareness, password safety, and secure digital practices for students.",
                    LocalDateTime.now().plusDays(9).withHour(10).withMinute(30),
                    new BigDecimal("0.00"),
                    180
            ));
            seedEvent(eventRepository, buildEvent(
                    "Data Analytics Workshop",
                    "Mathematics Department",
                    EventCategory.DEPARTMENT,
                    EventType.WORKSHOP,
                    "Analytics Lab",
                    "A practical workshop on dashboards, Excel automation, and introductory data storytelling for campus project teams.",
                    LocalDateTime.now().plusDays(10).withHour(13).withMinute(0),
                    new BigDecimal("129.00"),
                    100
            ));
            seedEvent(eventRepository, buildEvent(
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
            seedEvent(eventRepository, buildEvent(
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
            seedEvent(eventRepository, buildEvent(
                    "Photography Club Street Frame Walk",
                    "Photography Club",
                    EventCategory.CLUB,
                    EventType.WORKSHOP,
                    "Campus Lakefront",
                    "A guided photo walk covering composition basics, portrait framing, and editing tips for beginner shutter enthusiasts.",
                    LocalDateTime.now().plusDays(14).withHour(6).withMinute(30),
                    new BigDecimal("59.00"),
                    80
            ));
            seedEvent(eventRepository, buildEvent(
                    "Literary Club Open Mic",
                    "Literary Club",
                    EventCategory.CLUB,
                    EventType.CULTURAL,
                    "Mini Auditorium",
                    "An evening of poetry, storytelling, stand-up reading, and spoken word performances by student artists and club members.",
                    LocalDateTime.now().plusDays(14).withHour(18).withMinute(0),
                    new BigDecimal("29.00"),
                    140
            ));
            seedEvent(eventRepository, buildEvent(
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
            seedEvent(eventRepository, buildEvent(
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
            seedEvent(eventRepository, buildEvent(
                    "TechFest AI Build Sprint",
                    "TechFest Core Team",
                    EventCategory.TECH_FEST,
                    EventType.WORKSHOP,
                    "AI Experience Lab",
                    "A hands-on build sprint featuring prompt design, model integration, and mini product demos for cross-disciplinary student teams.",
                    LocalDateTime.now().plusDays(17).withHour(9).withMinute(30),
                    new BigDecimal("199.00"),
                    160
            ));
            seedEvent(eventRepository, buildEvent(
                    "Web3 and Startup Futures Webinar",
                    "TechFest Core Team",
                    EventCategory.TECH_FEST,
                    EventType.WEBINAR,
                    "Virtual Event Hall",
                    "A remote expert session discussing startup ecosystems, product building, and future-ready tech opportunities for graduates.",
                    LocalDateTime.now().plusDays(18).withHour(19).withMinute(0),
                    new BigDecimal("49.00"),
                    500
            ));
            seedEvent(eventRepository, buildEvent(
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
            seedEvent(eventRepository, buildEvent(
                    "Interdepartment Sports Meet Launch",
                    "Physical Education Department",
                    EventCategory.COLLEGE,
                    EventType.CULTURAL,
                    "Central Ground",
                    "The official opening event for the interdepartment sports calendar with march-past, team introductions, and fitness showcases.",
                    LocalDateTime.now().plusDays(22).withHour(8).withMinute(30),
                    new BigDecimal("0.00"),
                    350
            ));
            seedEvent(eventRepository, buildEvent(
                    "Career Readiness and Placement Talk",
                    "Placement Cell",
                    EventCategory.COLLEGE,
                    EventType.SEMINAR,
                    "Convention Hall",
                    "A campus-wide session on resume strategy, interview preparation, aptitude planning, and internship readiness for final-year students.",
                    LocalDateTime.now().plusDays(24).withHour(10).withMinute(0),
                    new BigDecimal("0.00"),
                    260
            ));
            seedEvent(eventRepository, buildEvent(
                    "Green Campus Sustainability Drive",
                    "NSS Unit",
                    EventCategory.COLLEGE,
                    EventType.WORKSHOP,
                    "Eco Activity Zone",
                    "A collaborative event with tree planting, waste segregation training, and student-led ideas for sustainable campus living.",
                    LocalDateTime.now().plusDays(26).withHour(15).withMinute(30),
                    new BigDecimal("19.00"),
                    200
            ));
        };
    }

    private void seedEvent(EventRepository eventRepository, Event event) {
        if (!eventRepository.existsByNameIgnoreCase(event.getName())) {
            eventRepository.save(event);
        }
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
