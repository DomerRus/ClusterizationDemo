package ru.ityce4ka.userservice.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ityce4ka.userservice.model.RoleModel;
import ru.ityce4ka.userservice.service.RoleService;
import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleResorce {

    @Autowired
    RoleService roleService;

    @GetMapping
    ResponseEntity<List<RoleModel>> getAll(){
        return roleService.findAll()
                .filter(list -> !list.isEmpty())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
