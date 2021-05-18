package ru.ityce4ka.routeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ityce4ka.routeservice.model.ClusterModel;

public interface ClustersRepository extends JpaRepository<ClusterModel, Long> {
}
