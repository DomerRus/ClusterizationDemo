package ru.ityce4ka.routeservice.service;

import org.springframework.http.HttpStatus;
import ru.ityce4ka.routeservice.model.RoleModel;
import ru.ityce4ka.routeservice.model.UserModel;
import ru.ityce4ka.routeservice.repository.RoleRepository;
import ru.ityce4ka.routeservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<List<UserModel>> findByRole(String name){
        return Optional.of(
                userEntityRepository.findByRoleName(name)
        );
    }

    public HttpStatus setRole(Long userId, Long roleId){
        Optional<RoleModel> roleModel = roleEntityRepository.findById(roleId);
        if(roleModel.isPresent()){
            Optional<UserModel> userModel = userEntityRepository.findById(userId);
            if(userModel.isPresent()){
                UserModel user = userModel.get();
                user.setRole(roleModel.get());
                userEntityRepository.save(user);
                return HttpStatus.OK;
            } else {
                return HttpStatus.BAD_REQUEST;
            }

        } else {
            return HttpStatus.BAD_REQUEST;
        }
    }


    public Optional<List<UserModel>> findAll(){
        return Optional.of(
                userEntityRepository.findAll()
        );
    }


}