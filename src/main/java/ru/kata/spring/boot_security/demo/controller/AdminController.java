package ru.kata.spring.boot_security.demo.controller;

import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    // Главная страница администратора
    @GetMapping
    public String adminPage(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("users", userService.getAllUsers());
        return "admin";
    }

    // Форма для создания нового пользователя
    @GetMapping("/create")
    public String showCreateForm(Model model, Principal principal) {
        String username = principal.getName();
        User currentUser = userService.findByUsername(username);
        model.addAttribute("user", currentUser);
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", roleService.findAll());
        return "create_user";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("newUser") User newUser,
                             @RequestParam("roles") List<Long> roleIds) {
        Set<ru.kata.spring.boot_security.demo.model.Role> roles = new HashSet<>();
        for (Long roleId : roleIds) {
            ru.kata.spring.boot_security.demo.model.Role role = roleService.findById(roleId);
            if (role != null) {
                roles.add(role);
            }
        }
        newUser.setRoles(roles);
        userService.createUser(newUser);
        return "redirect:/admin";
    }

    // Форма редактирования пользователя
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin";
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());
        return "edit_user";
    }

    // Обновление данных пользователя
    @PostMapping("/edit")
    public String updateUser(@ModelAttribute("user") User user, @RequestParam("roles") List<Long> roleIds) {
        Set<ru.kata.spring.boot_security.demo.model.Role> roles = new HashSet<>();
        for (Long roleId : roleIds) {
            ru.kata.spring.boot_security.demo.model.Role role = roleService.findById(roleId);
            if (role != null) {
                roles.add(role);
            }
        }
        user.setRoles(roles);
        userService.updateUser(user);
        return "redirect:/admin";
    }

    // Форма подтверждения удаления пользователя
    @GetMapping("/delete")
    public String showDeleteConfirmation(@RequestParam("id") Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin";
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());
        return "delete_user";
    }

    // Удаление пользователя
    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }
}
