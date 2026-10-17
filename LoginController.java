package com.careeriq.controller;

import com.careeriq.model.User;
import com.careeriq.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        Optional<User> authenticatedUser =
                userService.authenticate(email, password);

        if (authenticatedUser.isPresent()) {

            User user = authenticatedUser.get();

            session.setAttribute("loggedInUser", user.getEmail());
            session.setAttribute("loggedInUserId", user.getId());
            session.setAttribute("loggedInUserName", user.getFullName());

            return "redirect:/dashboard";
        }

        model.addAttribute(
                "errorMessage",
                "Invalid email or password. Please try again.");

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
