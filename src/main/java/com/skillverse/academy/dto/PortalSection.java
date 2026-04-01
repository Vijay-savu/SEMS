package com.skillverse.academy.dto;

import com.skillverse.academy.model.Event;
import java.util.List;

public record PortalSection(
        String title,
        String description,
        List<Event> events
) {
}
