package com.capstone.huddle.welcome.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
public class ViewController {

    @GetMapping({"/", "/index", "/home"})
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "user/signup";
    }

    @GetMapping("/articles")
    public String articles(Model model) {
        model.addAttribute("showUserArticles", false);
        return "article/articles";
    }

    @GetMapping("/my-articles")
    public String myArticles(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        model.addAttribute("showUserArticles", true);
        model.addAttribute("username", userDetails.getUsername());
        return "article/articles";
    }

    @GetMapping("/articles/{id}")
    public String article(@PathVariable String id, Model model) {
        try {
            UUID articleId = UUID.fromString(id);
            model.addAttribute("articleId", articleId.toString());
            return "article/articleDetail";
        } catch (IllegalArgumentException e) {
            return "redirect:/articles";
        }
    }

    @GetMapping("/articles/create")
    public String createArticle(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        return "article/article-editCreate";
    }

    @GetMapping("/articles/edit/{id}")
    public String editArticle(@PathVariable String id, Model model,
                              @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            UUID articleId = UUID.fromString(id);
            model.addAttribute("articleId", articleId.toString());
            model.addAttribute("isEdit", true);
            return "article/article-editCreate";
        } catch (IllegalArgumentException e) {
            return "redirect:/articles";
        }
    }

    @GetMapping("/profile")
    public String ownProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        return "user/profile";
    }

    @GetMapping("/users/{userId}")
    public String userProfile(@PathVariable String userId, Model model) {
        try {
            UUID userUuid = UUID.fromString(userId);
            model.addAttribute("userId", userUuid.toString());
            return "user/profile";
        } catch (IllegalArgumentException e) {
            return "redirect:/";
        }
    }

    @GetMapping("/notifications")
    public String notifications(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        return "notifications/notifications";
    }

    @GetMapping("/admin/reports")
    public String adminReports(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || !userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/";
        }
        return "admin/reports";
    }
}
