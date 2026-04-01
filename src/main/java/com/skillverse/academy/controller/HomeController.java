package com.skillverse.academy.controller;

import com.skillverse.academy.dto.ParticipantLoginForm;
import com.skillverse.academy.dto.ParticipantRegisterForm;
import com.skillverse.academy.dto.RegistrationForm;
import com.skillverse.academy.model.Account;
import com.skillverse.academy.model.Event;
import com.skillverse.academy.model.EventCategory;
import com.skillverse.academy.model.EventType;
import com.skillverse.academy.model.ParticipantType;
import com.skillverse.academy.service.AccountService;
import com.skillverse.academy.service.EventService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
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

    private final AccountService accountService;
    private final EventService eventService;

    public HomeController(AccountService accountService, EventService eventService) {
        this.accountService = accountService;
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
            Model model,
            HttpSession session
    ) {
        populatePortalModel(department, type, category, participantType, model, session);
        return "index";
    }

    @GetMapping("/portal")
    public String portal(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EventType type,
            @RequestParam(required = false) EventCategory category,
            @RequestParam(required = false) ParticipantType participantType,
            Model model,
            HttpSession session
    ) {
        populatePortalModel(department, type, category, participantType, model, session);
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
            RedirectAttributes redirectAttributes
    ) {
        validateParticipantRegisterForm(participantRegisterForm, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentParticipantType", participantRegisterForm.getParticipantType());
            return "participant-register";
        }

        try {
            accountService.registerParticipant(participantRegisterForm);
        } catch (IllegalArgumentException ex) {
            bindingResult.addError(new FieldError(
                    "participantRegisterForm",
                    "email",
                    participantRegisterForm.getEmail(),
                    false,
                    null,
                    null,
                    ex.getMessage()
            ));
            model.addAttribute("currentParticipantType", participantRegisterForm.getParticipantType());
            return "participant-register";
        }

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

        try {
            Account account = accountService.authenticateParticipant(participantLoginForm);
            session.setAttribute(PARTICIPANT_EMAIL_SESSION_KEY, account.getEmail());
            session.setAttribute(PARTICIPANT_TYPE_SESSION_KEY, account.getParticipantType().name());
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("invalidLogin", "Invalid email or password.");
            model.addAttribute("currentParticipantType", participantLoginForm.getParticipantType());
            return "participant-login";
        }
        return portalRedirect(participantLoginForm.getParticipantType());
    }

    @GetMapping("/events/{id}")
    public String eventDetails(
            @PathVariable String id,
            @RequestParam(required = false) ParticipantType participantType,
            Model model,
            HttpSession session
    ) {
        ParticipantType effectiveParticipantType = resolveParticipantType(participantType, session);

        if (effectiveParticipantType != null && !isParticipantLoggedIn(session, effectiveParticipantType)) {
            return loginRedirect(effectiveParticipantType);
        }

        Event event = eventService.getPublishedEvent(id);
        RegistrationForm registrationForm = new RegistrationForm();
        if (effectiveParticipantType != null) {
            registrationForm.setParticipantType(effectiveParticipantType);
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
        if (isParticipantLoggedIn(session, registrationForm.getParticipantType())) {
            return "redirect:/events/" + id + "?participantType=" + registrationForm.getParticipantType().name();
        }

        return "redirect:/my-registrations?email="
                + UriUtils.encode(registrationForm.getAttendeeEmail(), StandardCharsets.UTF_8);
    }

    @GetMapping("/my-registrations")
    public String myRegistrations(
            @RequestParam(required = false) String email,
            Model model,
            HttpSession session
    ) {
        String effectiveEmail = email;
        Object sessionEmail = session.getAttribute(PARTICIPANT_EMAIL_SESSION_KEY);
        if ((effectiveEmail == null || effectiveEmail.isBlank()) && sessionEmail instanceof String) {
            effectiveEmail = (String) sessionEmail;
        }

        model.addAttribute("lookupEmail", effectiveEmail == null ? "" : effectiveEmail);
        if (effectiveEmail != null && !effectiveEmail.isBlank()) {
            model.addAttribute("registrations", eventService.getRegistrationsForEmail(effectiveEmail));
        }
        return "my-registrations";
    }

    private void populatePortalModel(
            String department,
            EventType type,
            EventCategory category,
            ParticipantType participantType,
            Model model,
            HttpSession session
    ) {
        ParticipantType effectiveParticipantType = resolveParticipantType(participantType, session);
        model.addAttribute("sections", eventService.getPortalSections(department, type, category));
        model.addAttribute("hostUnits", eventService.getHostUnits());
        model.addAttribute("selectedDepartment", department == null ? "" : department);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedParticipantType", effectiveParticipantType);
    }

    private ParticipantType resolveParticipantType(ParticipantType participantType, HttpSession session) {
        if (participantType != null) {
            return participantType;
        }

        Object sessionType = session.getAttribute(PARTICIPANT_TYPE_SESSION_KEY);
        if (sessionType instanceof String value) {
            try {
                return ParticipantType.valueOf(value);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }

        return null;
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

    private String loginRedirect(ParticipantType participantType) {
        return "redirect:/participant-login?participantType=" + participantType.name();
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
