package ru.ityce4ka.routeservice.repository;

import ru.ityce4ka.routeservice.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    UserModel findByLogin(String login);
    List<UserModel> findByRoleName(String name);
}
