package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String index(Model model) {
        User admin =  userService.findEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("admin", admin);
        model.addAttribute("user", new User());
        model.addAttribute("users", userService.show());
        return "/admin/index";
    }


    @GetMapping("/user")
    public String id(Model model) {
        User admin =  userService.findEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("user", admin);
        return "/user/profile";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("user", new User());
        return "/admin/new";
    }

    @PostMapping()
    public String createUser(@ModelAttribute User user, BindingResult bindingResult
            , @RequestParam(value = "roles", required = false) String[] roles) {
        if (bindingResult.hasErrors()){
            return "/admin/new";
        }
        userService.save(user,roles);
        return "redirect:/admin";
    }

    @PostMapping("/user/edit")
    public String update(@RequestParam int id, @ModelAttribute User user,
                         @RequestParam(value = "roles", required = false) String[] roles) {
        userService.update(id, user,roles);
        return "redirect:/admin";
    }

    @PostMapping("/user/delete")
    public String delete(@RequestParam int id){
        userService.delete(id);
        return "redirect:/admin";
    }
}
