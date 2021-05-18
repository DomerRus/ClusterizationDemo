package ru.ityce4ka.routeservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ityce4ka.routeservice.model.RoleModel;
import ru.ityce4ka.routeservice.repository.RoleRepository;


import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleEntityRepository;

    public Optional<List<RoleModel>> findAll(){
        return Optional.of(
                roleEntityRepository.findAll()
        );
    }
}
