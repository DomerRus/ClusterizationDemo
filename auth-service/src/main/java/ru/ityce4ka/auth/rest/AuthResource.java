package ru.ityce4ka.auth.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ityce4ka.auth.config.jwt.JwtProvider;
import ru.ityce4ka.auth.model.AuthRequestModel;
import ru.ityce4ka.auth.model.AuthResponseModel;
import ru.ityce4ka.auth.model.UserModel;
import ru.ityce4ka.auth.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthResource {

    @Autowired
    UserService userService;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping
    ResponseEntity<AuthResponseModel> auth(@RequestBody AuthRequestModel authData){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        UserModel user =  userService.findByLogin(authData.getLogin());
        if(user != null && encoder.matches(authData.getPassword(), user.getPassword())){
            return ResponseEntity.ok(new AuthResponseModel(
                    jwtProvider.generateToken(user.getLogin())
            ));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


}
