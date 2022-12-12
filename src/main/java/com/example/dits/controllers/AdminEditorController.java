package com.example.dits.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminEditorController {

    @GetMapping("/users")
    public String getUsers(ModelMap model) {
        model.addAttribute("title", "Editor user");
        return "/admin/user-editor";
    }
}
