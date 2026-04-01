package com.skillverse.academy.dto;

import com.skillverse.academy.model.ParticipantType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegistrationForm {

    @NotNull
    private ParticipantType participantType = ParticipantType.INTERNAL;

    @NotBlank
    private String attendeeName;

    @NotBlank
    @Email
    private String attendeeEmail;

    private String attendeeDepartment;

    private String attendeeCollegeName;

    @Min(1)
    @Max(6)
    private Integer ticketsBooked = 1;

    public ParticipantType getParticipantType() {
        return participantType;
    }

    public void setParticipantType(ParticipantType participantType) {
        this.participantType = participantType;
    }

    public String getAttendeeName() {
        return attendeeName;
    }

    public void setAttendeeName(String attendeeName) {
        this.attendeeName = attendeeName;
    }

    public String getAttendeeEmail() {
        return attendeeEmail;
    }

    public void setAttendeeEmail(String attendeeEmail) {
        this.attendeeEmail = attendeeEmail;
    }

    public String getAttendeeDepartment() {
        return attendeeDepartment;
    }

    public void setAttendeeDepartment(String attendeeDepartment) {
        this.attendeeDepartment = attendeeDepartment;
    }

    public String getAttendeeCollegeName() {
        return attendeeCollegeName;
    }

    public void setAttendeeCollegeName(String attendeeCollegeName) {
        this.attendeeCollegeName = attendeeCollegeName;
    }

    public Integer getTicketsBooked() {
        return ticketsBooked;
    }

    public void setTicketsBooked(Integer ticketsBooked) {
        this.ticketsBooked = ticketsBooked;
    }
}
