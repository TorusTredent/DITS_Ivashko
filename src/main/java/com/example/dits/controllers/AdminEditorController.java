package com.example.dits.controllers;

import com.example.dits.dto.UserInfoDTO;
import com.example.dits.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminEditorController {

    private final UserService userService;

    @GetMapping("/users")
    public String getUsers(ModelMap model) {
        model.addAttribute("userList", getAll());
        model.addAttribute("title", "Editor user");
        return "/admin/user-editor";
    }

    @ResponseBody
    @GetMapping("/get/all")
    public List<UserInfoDTO> getAll() {
        return userService.getAllUsers();
    }

    @ResponseBody
    @DeleteMapping("/user")
    public List<UserInfoDTO> deleteById(@RequestParam int id) {
        userService.deleteById(id);
        return getAll();
    }

    @ResponseBody
    @PostMapping("/add/user")
    public List<UserInfoDTO> addUser(@RequestBody UserInfoDTO userInfoDTO) {
        userService.save(userInfoDTO);
        return getAll();
    }

    @ResponseBody
    @PutMapping("/user")
    public List<UserInfoDTO> updateUser(@RequestBody UserInfoDTO userInfoDTO) {
        userService.update(userInfoDTO);
        return getAll();
    }
}
