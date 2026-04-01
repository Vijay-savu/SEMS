package com.skillverse.academy.controller;

import com.skillverse.academy.dto.RegistrationForm;
import com.skillverse.academy.model.Event;
import com.skillverse.academy.model.EventCategory;
import com.skillverse.academy.model.EventType;
import com.skillverse.academy.service.EventService;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

@Controller
public class HomeController {

    private final EventService eventService;

    public HomeController(EventService eventService) {
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

    @GetMapping("/")
    public String root(Authentication authentication) {
        if (isAuthenticated(authentication)) {
            return "redirect:" + resolveHome(authentication);
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (isAuthenticated(authentication)) {
            return "redirect:" + resolveHome(authentication);
        }
        return "login";
    }

    @GetMapping("/portal")
    public String home(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EventType type,
            @RequestParam(required = false) EventCategory category,
            Model model
    ) {
        model.addAttribute("sections", eventService.getPortalSections(department, type, category));
        model.addAttribute("featuredEvents", eventService.getUpcomingEvents(department, type, category).stream().limit(3).toList());
        model.addAttribute("hostUnits", eventService.getHostUnits());
        model.addAttribute("selectedDepartment", department == null ? "" : department);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedCategory", category);
        return "index";
    }

    @GetMapping("/events/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        Event event = eventService.getPublishedEvent(id);
        model.addAttribute("event", event);
        model.addAttribute("registrationForm", new RegistrationForm());
        return "event-detail";
    }

    @PostMapping("/events/{id}/register")
    public String registerForEvent(
            @PathVariable Long id,
            @Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Event event = eventService.getPublishedEvent(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("event", event);
            return "event-detail";
        }

        eventService.registerForEvent(id, registrationForm);
        redirectAttributes.addFlashAttribute("successMessage", "Registration completed successfully.");
        return "redirect:/my-registrations?email="
                + UriUtils.encode(registrationForm.getAttendeeEmail(), StandardCharsets.UTF_8);
    }

    @GetMapping("/my-registrations")
    public String myRegistrations(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("lookupEmail", email == null ? "" : email);
        if (email != null && !email.isBlank()) {
            model.addAttribute("registrations", eventService.getRegistrationsForEmail(email));
        }
        return "my-registrations";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private String resolveHome(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        return isAdmin ? "/admin" : "/portal";
    }
}
