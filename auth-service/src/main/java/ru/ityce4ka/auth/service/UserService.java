package ru.ityce4ka.auth.service;

import ru.ityce4ka.auth.model.RoleModel;
import ru.ityce4ka.auth.model.UserModel;
import ru.ityce4ka.auth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userEntityRepository;
    @Autowired
    private RoleRepository roleEntityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserModel saveUser(UserModel user) {
        RoleModel userRole = roleEntityRepository.findByName("ROLE_USER");
        user.setRole(userRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userEntityRepository.save(user);
    }

    public UserModel findByLogin(String login) {
        return userEntityRepository.findByLogin(login);
    }

    public UserModel findByLoginAndPassword(String login, String password) {
        UserModel user = findByLogin(login);
        if (user != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}