package com.skillverse.academy.controller;

import com.skillverse.academy.dto.EventForm;
import com.skillverse.academy.model.EventCategory;
import com.skillverse.academy.model.EventType;
import com.skillverse.academy.service.EventService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final EventService eventService;

    public AdminController(EventService eventService) {
        this.eventService = eventService;
    }

    @ModelAttribute("eventTypes")
    EventType[] eventTypes() {
        return EventType.values();
    }

    @ModelAttribute("eventCategories")
    EventCategory[] eventCategories() {
        return EventCategory.values();
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("stats", eventService.getDashboardStats());
        model.addAttribute("departmentCount", eventService.getDepartmentCount());
        model.addAttribute("events", eventService.getAdminEvents());
        return "admin/dashboard";
    }

    @GetMapping("/admin/events/new")
    public String createEventPage(Model model) {
        model.addAttribute("eventForm", new EventForm());
        model.addAttribute("pageTitle", "Create Event");
        model.addAttribute("formAction", "/admin/events");
        return "admin/event-form";
    }

    @PostMapping("/admin/events")
    public String createEvent(
            @Valid @ModelAttribute("eventForm") EventForm eventForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Create Event");
            model.addAttribute("formAction", "/admin/events");
            return "admin/event-form";
        }

        eventService.createEvent(eventForm);
        redirectAttributes.addFlashAttribute("successMessage", "Event created successfully.");
        return "redirect:/admin";
    }

    @GetMapping("/admin/events/{id}/edit")
    public String editEventPage(@PathVariable Long id, Model model) {
        model.addAttribute("eventForm", eventService.toForm(eventService.getEvent(id)));
        model.addAttribute("pageTitle", "Edit Event");
        model.addAttribute("formAction", "/admin/events/" + id);
        return "admin/event-form";
    }

    @PostMapping("/admin/events/{id}")
    public String updateEvent(
            @PathVariable Long id,
            @Valid @ModelAttribute("eventForm") EventForm eventForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Event");
            model.addAttribute("formAction", "/admin/events/" + id);
            return "admin/event-form";
        }

        eventService.updateEvent(id, eventForm);
        redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/events/{id}/delete")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        eventService.deleteEvent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully.");
        return "redirect:/admin";
    }

    @GetMapping("/admin/events/{id}/registrations")
    public String viewRegistrations(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getEvent(id));
        model.addAttribute("registrations", eventService.getRegistrationsForEvent(id));
        return "admin/registrations";
    }
}
