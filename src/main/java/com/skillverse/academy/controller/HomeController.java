package com.skillverse.academy.controller;

import com.skillverse.academy.dto.ParticipantLoginForm;
import com.skillverse.academy.dto.ParticipantRegisterForm;
import com.skillverse.academy.dto.RegistrationForm;
import com.skillverse.academy.model.Event;
import com.skillverse.academy.model.EventCategory;
import com.skillverse.academy.model.EventType;
import com.skillverse.academy.model.ParticipantType;
import com.skillverse.academy.service.EventService;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

@Controller
public class HomeController {

    private static final String PARTICIPANT_EMAIL_SESSION_KEY = "participantEmail";
    private static final String PARTICIPANT_TYPE_SESSION_KEY = "participantType";
    private static final String REGISTERED_PARTICIPANT_EMAIL_PREFIX = "registeredParticipantEmail:";
    private static final String REGISTERED_PARTICIPANT_PASSWORD_PREFIX = "registeredParticipantPassword:";

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

    @ModelAttribute("participantTypes")
    ParticipantType[] participantTypes() {
        return ParticipantType.values();
    }

    @ModelAttribute("participantLoggedIn")
    boolean participantLoggedIn(HttpSession session) {
        return session.getAttribute(PARTICIPANT_EMAIL_SESSION_KEY) instanceof String;
    }

    @ModelAttribute("participantSessionEmail")
    String participantSessionEmail(HttpSession session) {
        Object value = session.getAttribute(PARTICIPANT_EMAIL_SESSION_KEY);
        return value instanceof String ? (String) value : null;
    }

    @ModelAttribute("participantSessionType")
    String participantSessionType(HttpSession session) {
        Object value = session.getAttribute(PARTICIPANT_TYPE_SESSION_KEY);
        return value instanceof String ? (String) value : null;
    }

    @GetMapping("/")
    public String root(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EventType type,
            @RequestParam(required = false) EventCategory category,
            @RequestParam(required = false) ParticipantType participantType,
            Model model
    ) {
        populatePortalModel(department, type, category, participantType, model);
        return "index";
    }

    @GetMapping("/portal")
    public String portal(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EventType type,
            @RequestParam(required = false) EventCategory category,
            @RequestParam(required = false) ParticipantType participantType,
            Model model
    ) {
        populatePortalModel(department, type, category, participantType, model);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/participant-register")
    public String participantRegister(
            @RequestParam ParticipantType participantType,
            Model model
    ) {
        ParticipantRegisterForm registerForm = new ParticipantRegisterForm();
        registerForm.setParticipantType(participantType);
        model.addAttribute("participantRegisterForm", registerForm);
        model.addAttribute("currentParticipantType", participantType);
        return "participant-register";
    }

    @PostMapping("/participant-register")
    public String participantRegisterSubmit(
            @Valid @ModelAttribute("participantRegisterForm") ParticipantRegisterForm participantRegisterForm,
            BindingResult bindingResult,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        validateParticipantRegisterForm(participantRegisterForm, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentParticipantType", participantRegisterForm.getParticipantType());
            return "participant-register";
        }

        session.setAttribute(registeredEmailKey(participantRegisterForm.getParticipantType()), participantRegisterForm.getEmail());
        session.setAttribute(registeredPasswordKey(participantRegisterForm.getParticipantType()), participantRegisterForm.getPassword());
        redirectAttributes.addFlashAttribute("participantRegisterSuccess", "Registration completed. Please login to continue.");
        return "redirect:/participant-login?participantType=" + participantRegisterForm.getParticipantType().name();
    }

    @GetMapping("/participant-login")
    public String participantLogin(
            @RequestParam ParticipantType participantType,
            Model model,
            HttpSession session
    ) {
        if (isParticipantLoggedIn(session, participantType)) {
            return portalRedirect(participantType);
        }

        if (!isParticipantRegistered(session, participantType)) {
            return registerRedirect(participantType);
        }

        ParticipantLoginForm loginForm = new ParticipantLoginForm();
        loginForm.setParticipantType(participantType);
        model.addAttribute("participantLoginForm", loginForm);
        model.addAttribute("currentParticipantType", participantType);
        return "participant-login";
    }

    @GetMapping("/participant-logout")
    public String participantLogout(HttpSession session) {
        session.removeAttribute(PARTICIPANT_EMAIL_SESSION_KEY);
        session.removeAttribute(PARTICIPANT_TYPE_SESSION_KEY);
        return "redirect:/login";
    }

    @PostMapping("/participant-login")
    public String participantLoginSubmit(
            @Valid @ModelAttribute("participantLoginForm") ParticipantLoginForm participantLoginForm,
            BindingResult bindingResult,
            Model model,
            HttpSession session
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentParticipantType", participantLoginForm.getParticipantType());
            return "participant-login";
        }

        if (!isParticipantRegistered(session, participantLoginForm.getParticipantType())) {
            return registerRedirect(participantLoginForm.getParticipantType());
        }

        String registeredEmail = registeredEmailFor(session, participantLoginForm.getParticipantType());
        String registeredPassword = registeredPasswordFor(session, participantLoginForm.getParticipantType());
        if (!participantLoginForm.getEmail().equalsIgnoreCase(registeredEmail)
                || !participantLoginForm.getPassword().equals(registeredPassword)) {
            bindingResult.reject("invalidLogin", "Invalid email or password.");
            model.addAttribute("currentParticipantType", participantLoginForm.getParticipantType());
            return "participant-login";
        }

        session.setAttribute(PARTICIPANT_EMAIL_SESSION_KEY, participantLoginForm.getEmail());
        session.setAttribute(PARTICIPANT_TYPE_SESSION_KEY, participantLoginForm.getParticipantType().name());
        return portalRedirect(participantLoginForm.getParticipantType());
    }

    @GetMapping("/events/{id}")
    public String eventDetails(
            @PathVariable String id,
            @RequestParam(required = false) ParticipantType participantType,
            Model model,
            HttpSession session
    ) {
        if (participantType != null && !isParticipantLoggedIn(session, participantType)) {
            return loginRedirect(participantType);
        }

        Event event = eventService.getPublishedEvent(id);
        RegistrationForm registrationForm = new RegistrationForm();
        if (participantType != null) {
            registrationForm.setParticipantType(participantType);
        }
        String participantEmail = participantEmailFor(session, registrationForm.getParticipantType());
        if (participantEmail != null) {
            registrationForm.setAttendeeEmail(participantEmail);
        }
        model.addAttribute("event", event);
        model.addAttribute("registrationForm", registrationForm);
        return "event-detail";
    }

    @PostMapping("/events/{id}/register")
    public String registerForEvent(
            @PathVariable String id,
            @Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session
    ) {
        if (!isParticipantLoggedIn(session, registrationForm.getParticipantType())) {
            return loginRedirect(registrationForm.getParticipantType());
        }

        Event event = eventService.getPublishedEvent(id);
        validateParticipantFields(registrationForm, bindingResult);

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

    private void populatePortalModel(
            String department,
            EventType type,
            EventCategory category,
            ParticipantType participantType,
            Model model
    ) {
        model.addAttribute("sections", eventService.getPortalSections(department, type, category));
        model.addAttribute("hostUnits", eventService.getHostUnits());
        model.addAttribute("selectedDepartment", department == null ? "" : department);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedParticipantType", participantType);
    }

    private boolean isParticipantLoggedIn(HttpSession session, ParticipantType participantType) {
        if (participantType == null) {
            return false;
        }

        Object sessionEmail = session.getAttribute(PARTICIPANT_EMAIL_SESSION_KEY);
        Object sessionType = session.getAttribute(PARTICIPANT_TYPE_SESSION_KEY);
        return sessionEmail instanceof String
                && !((String) sessionEmail).isBlank()
                && participantType.name().equals(sessionType);
    }

    private String participantEmailFor(HttpSession session, ParticipantType participantType) {
        if (!isParticipantLoggedIn(session, participantType)) {
            return null;
        }
        return (String) session.getAttribute(PARTICIPANT_EMAIL_SESSION_KEY);
    }

    private boolean isParticipantRegistered(HttpSession session, ParticipantType participantType) {
        return registeredEmailFor(session, participantType) != null
                && registeredPasswordFor(session, participantType) != null;
    }

    private String registeredEmailFor(HttpSession session, ParticipantType participantType) {
        Object value = session.getAttribute(registeredEmailKey(participantType));
        return value instanceof String && !((String) value).isBlank() ? (String) value : null;
    }

    private String registeredPasswordFor(HttpSession session, ParticipantType participantType) {
        Object value = session.getAttribute(registeredPasswordKey(participantType));
        return value instanceof String && !((String) value).isBlank() ? (String) value : null;
    }

    private String registeredEmailKey(ParticipantType participantType) {
        return REGISTERED_PARTICIPANT_EMAIL_PREFIX + participantType.name();
    }

    private String registeredPasswordKey(ParticipantType participantType) {
        return REGISTERED_PARTICIPANT_PASSWORD_PREFIX + participantType.name();
    }

    private String loginRedirect(ParticipantType participantType) {
        return "redirect:/participant-login?participantType=" + participantType.name();
    }

    private String registerRedirect(ParticipantType participantType) {
        return "redirect:/participant-register?participantType=" + participantType.name();
    }

    private String portalRedirect(ParticipantType participantType) {
        return "redirect:/portal?participantType=" + participantType.name();
    }

    private void validateParticipantRegisterForm(
            ParticipantRegisterForm participantRegisterForm,
            BindingResult bindingResult
    ) {
        if (participantRegisterForm.getPassword() != null
                && participantRegisterForm.getConfirmPassword() != null
                && !participantRegisterForm.getPassword().equals(participantRegisterForm.getConfirmPassword())) {
            bindingResult.addError(new FieldError(
                    "participantRegisterForm",
                    "confirmPassword",
                    participantRegisterForm.getConfirmPassword(),
                    false,
                    null,
                    null,
                    "Passwords do not match."
            ));
        }
    }

    private void validateParticipantFields(RegistrationForm registrationForm, BindingResult bindingResult) {
        if (registrationForm.getParticipantType() == ParticipantType.INTERNAL
                && (registrationForm.getAttendeeDepartment() == null || registrationForm.getAttendeeDepartment().isBlank())) {
            bindingResult.addError(new FieldError(
                    "registrationForm",
                    "attendeeDepartment",
                    registrationForm.getAttendeeDepartment(),
                    false,
                    null,
                    null,
                    "Department is required for internal participants."
            ));
        }

        if (registrationForm.getParticipantType() == ParticipantType.EXTERNAL
                && (registrationForm.getAttendeeCollegeName() == null || registrationForm.getAttendeeCollegeName().isBlank())) {
            bindingResult.addError(new FieldError(
                    "registrationForm",
                    "attendeeCollegeName",
                    registrationForm.getAttendeeCollegeName(),
                    false,
                    null,
                    null,
                    "College name is required for external participants."
            ));
        }
    }
}
