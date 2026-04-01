package com.skillverse.academy.service;

import com.skillverse.academy.dto.DashboardStats;
import com.skillverse.academy.dto.EventForm;
import com.skillverse.academy.dto.PortalSection;
import com.skillverse.academy.dto.RegistrationForm;
import com.skillverse.academy.model.Event;
import com.skillverse.academy.model.EventCategory;
import com.skillverse.academy.model.EventType;
import com.skillverse.academy.model.ParticipantType;
import com.skillverse.academy.model.Registration;
import com.skillverse.academy.repository.EventRepository;
import com.skillverse.academy.repository.RegistrationRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public EventService(EventRepository eventRepository, RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    public List<Event> getUpcomingEvents(String department, EventType type, EventCategory category) {
        return eventRepository.findByPublishedTrueAndEventDateTimeAfterOrderByEventDateTimeAsc(LocalDateTime.now()).stream()
                .filter(event -> department == null || department.isBlank()
                        || event.getDepartment().toLowerCase().contains(department.trim().toLowerCase()))
                .filter(event -> type == null || event.getType() == type)
                .filter(event -> category == null || event.getCategory() == category)
                .toList();
    }

    public List<PortalSection> getPortalSections(String department, EventType type, EventCategory category) {
        List<Event> events = getUpcomingEvents(department, type, category);
        List<PortalSection> sections = new ArrayList<>();

        for (EventCategory currentCategory : EventCategory.values()) {
            List<Event> categoryEvents = events.stream()
                    .filter(event -> event.getCategory() == currentCategory)
                    .toList();
            if (!categoryEvents.isEmpty()) {
                sections.add(new PortalSection(
                        currentCategory.getDisplayName(),
                        currentCategory.getDescription(),
                        categoryEvents
                ));
            }
        }

        return sections;
    }

    public List<String> getHostUnits() {
        return eventRepository.findByPublishedTrueAndEventDateTimeAfterOrderByEventDateTimeAsc(LocalDateTime.now()).stream()
                .map(Event::getDepartment)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    public List<Event> getAdminEvents() {
        return eventRepository.findAll().stream()
                .sorted(Comparator.comparing(Event::getEventDateTime))
                .toList();
    }

    public Event getPublishedEvent(String id) {
        Event event = getEvent(id);
        if (!event.isPublished()) {
            throw new IllegalArgumentException("This event is not open for public booking.");
        }
        return event;
    }

    public Event getEvent(String id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));
    }

    public EventForm toForm(Event event) {
        EventForm form = new EventForm();
        form.setName(event.getName());
        form.setDepartment(event.getDepartment());
        form.setCategory(event.getCategory());
        form.setType(event.getType());
        form.setVenue(event.getVenue());
        form.setDescription(event.getDescription());
        form.setEventDateTime(event.getEventDateTime());
        form.setTicketPrice(event.getTicketPrice());
        form.setCapacity(event.getCapacity());
        form.setPublished(event.isPublished());
        return form;
    }

    public Event createEvent(EventForm form) {
        Event event = new Event();
        applyEventForm(form, event);
        event.setAvailableSeats(form.getCapacity());
        return eventRepository.save(event);
    }

    public Event updateEvent(String id, EventForm form) {
        Event event = getEvent(id);
        int bookedSeats = event.getCapacity() - event.getAvailableSeats();
        if (form.getCapacity() < bookedSeats) {
            throw new IllegalArgumentException("Capacity cannot be lower than already booked seats.");
        }

        applyEventForm(form, event);
        event.setAvailableSeats(form.getCapacity() - bookedSeats);
        return eventRepository.save(event);
    }

    public void deleteEvent(String id) {
        registrationRepository.deleteByEventId(id);
        eventRepository.delete(getEvent(id));
    }

    public Registration registerForEvent(String eventId, RegistrationForm form) {
        Event event = getPublishedEvent(eventId);
        if (event.getAvailableSeats() < form.getTicketsBooked()) {
            throw new IllegalArgumentException("Not enough tickets are available for this event.");
        }

        Registration registration = new Registration();
        registration.setEventId(event.getId());
        registration.setEvent(event);
        registration.setParticipantType(form.getParticipantType());
        registration.setAttendeeName(form.getAttendeeName());
        registration.setAttendeeEmail(form.getAttendeeEmail());
        registration.setAttendeeDepartment(form.getParticipantType() == ParticipantType.INTERNAL
                ? safeValue(form.getAttendeeDepartment())
                : "");
        registration.setAttendeeCollegeName(form.getParticipantType() == ParticipantType.EXTERNAL
                ? safeValue(form.getAttendeeCollegeName())
                : "");
        registration.setTicketsBooked(form.getTicketsBooked());
        registration.setRegisteredAt(LocalDateTime.now());

        event.setAvailableSeats(event.getAvailableSeats() - form.getTicketsBooked());
        eventRepository.save(event);
        return registrationRepository.save(registration);
    }

    public List<Registration> getRegistrationsForEmail(String email) {
        return attachEvents(registrationRepository.findByAttendeeEmailIgnoreCaseOrderByRegisteredAtDesc(email));
    }

    public List<Registration> getRegistrationsForEvent(String eventId) {
        return attachEvents(registrationRepository.findByEventIdOrderByRegisteredAtDesc(eventId));
    }

    public DashboardStats getDashboardStats() {
        long totalBookedSeats = registrationRepository.findAll().stream()
                .mapToLong(registration -> registration.getTicketsBooked() == null ? 0 : registration.getTicketsBooked())
                .sum();
        return new DashboardStats(
                eventRepository.count(),
                eventRepository.countByPublishedTrue(),
                registrationRepository.count(),
                totalBookedSeats
        );
    }

    public long getDepartmentCount() {
        return eventRepository.findAll().stream()
                .map(Event::getDepartment)
                .filter(department -> department != null && !department.isBlank())
                .distinct()
                .count();
    }

    private void applyEventForm(EventForm form, Event event) {
        event.setName(form.getName());
        event.setDepartment(form.getDepartment());
        event.setCategory(form.getCategory());
        event.setType(form.getType());
        event.setVenue(form.getVenue());
        event.setDescription(form.getDescription());
        event.setEventDateTime(form.getEventDateTime());
        event.setTicketPrice(form.getTicketPrice());
        event.setCapacity(form.getCapacity());
        event.setPublished(form.isPublished());
    }

    private String safeValue(String value) {
        return value == null ? "" : value.trim();
    }

    private List<Registration> attachEvents(List<Registration> registrations) {
        if (registrations.isEmpty()) {
            return registrations;
        }

        Map<String, Event> eventsById = new HashMap<>();
        for (Event event : eventRepository.findAll()) {
            eventsById.put(event.getId(), event);
        }

        registrations.forEach(registration -> registration.setEvent(eventsById.get(registration.getEventId())));
        return registrations;
    }
}
