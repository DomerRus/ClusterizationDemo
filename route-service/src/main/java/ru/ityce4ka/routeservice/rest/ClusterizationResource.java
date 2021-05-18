package ru.ityce4ka.routeservice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ityce4ka.routeservice.model.ClusterModel;
import ru.ityce4ka.routeservice.service.ClusterizationService;
import ru.ityce4ka.routeservice.service.RouteService;

import java.util.List;

@RestController
@RequestMapping("/clusters")
public class ClusterizationResource {

    @Autowired
    RouteService routeService;

    @Autowired
    ClusterizationService clusterizationService;


    @GetMapping("/{clusterId}")
    ResponseEntity<ClusterModel> getByClusterId(@PathVariable("clusterId") Long clusterId){
        return clusterizationService.findById(clusterId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    @GetMapping
    ResponseEntity<List<ClusterModel>> getAllClusters(@PathVariable("clusterId") Long clusterId){
        return clusterizationService.findAll()
                .filter(list -> !list.isEmpty())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/min-edge-algo")
    ResponseEntity<List<ClusterModel>> getOptimalClusterEdgeRoute(@RequestParam("clusterCount") Integer clusterCount,
                                                              @RequestParam(value = "clusterRadius",required = false) Double clusterRadius,
                                                              @RequestParam(value = "maxClusterSize",required = false) Integer maxClusterSize){
        if(clusterRadius == null) clusterRadius = 5000.0;
        if(maxClusterSize == null) maxClusterSize = 7;
        return routeService.minEdgesRouteAlgorithm(clusterRadius, clusterCount, maxClusterSize)
                .filter(list -> !list.isEmpty())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    @GetMapping("/min-size-algo")
    ResponseEntity<List<ClusterModel>> getOptimalClusterRoute(@RequestParam("clusterCount") Integer clusterCount,
                                                              @RequestParam(value = "clusterRadius",required = false) Double clusterRadius,
                                                              @RequestParam(value = "maxClusterSize",required = false) Integer maxClusterSize){
        if(clusterRadius == null) clusterRadius = 5000.0;
        if(maxClusterSize == null) maxClusterSize = 7;
        return routeService.minRouteAlgorithm(clusterRadius, clusterCount, maxClusterSize)
                .filter(list -> !list.isEmpty())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
