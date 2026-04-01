package com.skillverse.academy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class RegistrationForm {

    @NotBlank
    private String attendeeName;

    @NotBlank
    @Email
    private String attendeeEmail;

    @NotBlank
    private String attendeeDepartment;

    @Min(1)
    @Max(6)
    private Integer ticketsBooked = 1;

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

    public Integer getTicketsBooked() {
        return ticketsBooked;
    }

    public void setTicketsBooked(Integer ticketsBooked) {
        this.ticketsBooked = ticketsBooked;
    }
}
