package com.skillverse.academy.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = {HomeController.class, AdminController.class})
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException exception, Model model) {
        model.addAttribute("errorTitle", "Request could not be completed");
        model.addAttribute("errorMessage", exception.getMessage());
        return "error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request, Model model) {
        model.addAttribute("errorTitle", "Validation failed");
        model.addAttribute("errorMessage", "Please review the submitted values and try again.");
        return "error";
    }
}
