package com.skillverse.academy.dto;

public record DashboardStats(
        long totalEvents,
        long publishedEvents,
        long totalRegistrations,
        long totalBookedSeats
) {
}
