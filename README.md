# Smart Campus Event Management System

A syllabus-aligned full stack application for managing campus workshops, seminars, hackathons, and ticket bookings. The project demonstrates Spring Boot, Spring MVC, Spring Data JPA, validation, security, REST APIs, and a polished student/admin UI.

## What It Covers

- Student-facing event discovery and registration
- Admin CRUD for events
- Ticket availability tracking
- Booking history lookup by email
- REST API for upcoming events and dashboard stats
- JPA entity mapping and repository-driven persistence
- Form validation, exception handling, and Spring Security

## Tech Stack

- Spring Boot
- Spring MVC + Thymeleaf
- Spring Data JPA
- Spring Security
- H2 database for local demo
- MySQL runtime driver included for later upgrade
- HTML5, CSS3, responsive UI

## Demo Credentials

- Admin username: `admin`
- Admin password: `admin123`
- Student username: `student`
- Student password: `student123`

## Run Locally

```bash
mvn spring-boot:run
```

Open `http://localhost:3000`

## Key Routes

- `/` public event board
- `/events/{id}` event details and booking
- `/my-registrations` registration lookup
- `/admin` admin dashboard
- `/api/events` public REST API
- `/api/admin/stats` admin stats API
