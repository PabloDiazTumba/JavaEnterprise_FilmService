package com.example.filmservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("message", "Welcome to the FilmService Home Page!");
        return "home";  // Refererar till Thymeleaf-vyn home.html
    }
}