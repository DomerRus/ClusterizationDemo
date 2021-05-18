package ru.ityce4ka.userservice.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ityce4ka.userservice.model.RegistrationRequestModel;
import ru.ityce4ka.userservice.model.RegistrationResponseModel;
import ru.ityce4ka.userservice.model.UserModel;
import ru.ityce4ka.userservice.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserResource {

    @Autowired
    UserService userService;

    @GetMapping
    ResponseEntity<List<UserModel>> getAll(){
        return userService.findAll()
                .filter(list -> !list.isEmpty())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/admins")
    ResponseEntity<List<UserModel>> getAdmins(){
        log.info("Пользователь получает список администраторов...");
        return userService.findByRole("ROLE_ADMIN")
                .filter(list -> !list.isEmpty())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/register")
    ResponseEntity<RegistrationResponseModel> registrationUser(@RequestBody RegistrationRequestModel reg){
        log.info("Администратор пытается создать нового пользователя с логином {} и паролем {}", reg.getLogin(), reg.getPassword());
        if(!reg.getLogin().isEmpty()) {
            if(userService.findByLogin(reg.getLogin()) != null) return ResponseEntity.badRequest().build();
            return ResponseEntity.ok(
                new RegistrationResponseModel(
                    userService.saveUser(
                        new UserModel(
                            reg.getLogin(),
                            reg.getPassword()
                        )
                    )
                )
            );
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/getByRole/{roleName}")
    ResponseEntity<List<UserModel>> getByRole(@PathVariable("roleName") String roleName){
        return userService.findByRole(roleName)
                .filter(list -> !list.isEmpty())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }


    @PostMapping("/set-role")
    ResponseEntity<Void> setUserRole(@RequestParam("userId") Long userId,
                                     @RequestParam("roleId") Long roleId){
        return ResponseEntity.status(userService.setRole(userId,roleId)).build();

    }

}


