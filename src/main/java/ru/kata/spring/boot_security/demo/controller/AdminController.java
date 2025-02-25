package ru.kata.spring.boot_security.demo.controller;

import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    // Главная страница администратора
    @GetMapping
    public String adminPage(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("users", userService.getAllUsers());
        return "admin"; // Убедись, что шаблон admin.html существует
    }


    // Форма для создания нового пользователя
    @GetMapping("/create")
    public String showCreateForm(Model model, Principal principal) {
        String username = principal.getName();
        User currentUser = userService.findByUsername(username);

        model.addAttribute("user", currentUser); // ✅ Добавляем текущего пользователя
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", roleRepository.findAll());

        return "create_user"; // create_user.html
    }
    @PostMapping("/create")
    public String createUser(@ModelAttribute("newUser") User newUser,
                             @RequestParam("roles") List<Long> roleIds) {
        Set<ru.kata.spring.boot_security.demo.model.Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        newUser.setRoles(roles);
        userService.createUser(newUser);
        return "redirect:/admin"; // После создания пользователя перенаправление на страницу админа
    }

    // Форма редактирования пользователя
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin";
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "edit_user"; // edit_user.html
    }

    // Обновление данных пользователя
    @PostMapping("/edit")
    public String updateUser(@ModelAttribute("user") User user, @RequestParam("roles") List<Long> roleIds) {
        Set<ru.kata.spring.boot_security.demo.model.Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
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
        return "delete_user"; // delete_user.html
    }

    // Удаление пользователя
    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }
}
