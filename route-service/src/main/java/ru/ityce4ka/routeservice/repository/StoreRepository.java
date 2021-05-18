package ru.ityce4ka.routeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ityce4ka.routeservice.model.StoreModel;

public interface StoreRepository extends JpaRepository<StoreModel, Long> {

}