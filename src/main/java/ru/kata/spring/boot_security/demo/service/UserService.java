package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UserService extends UserDetailsService {
    void createUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    void deleteUserById(Long id);
    void updateUser(User user);
    User findByUsername(String username);
}
