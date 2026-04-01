package com.skillverse.academy.controller;

import com.skillverse.academy.dto.DashboardStats;
import com.skillverse.academy.model.Event;
import com.skillverse.academy.model.EventCategory;
import com.skillverse.academy.model.EventType;
import com.skillverse.academy.service.EventService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EventRestController {

    private final EventService eventService;

    public EventRestController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public List<Event> upcomingEvents(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EventType type,
            @RequestParam(required = false) EventCategory category
    ) {
        return eventService.getUpcomingEvents(department, type, category);
    }

    @GetMapping("/events/{id}")
    public Event eventDetails(@PathVariable String id) {
        return eventService.getPublishedEvent(id);
    }

    @GetMapping("/admin/stats")
    public DashboardStats adminStats() {
        return eventService.getDashboardStats();
    }
}
