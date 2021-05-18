package ru.ityce4ka.routeservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.ityce4ka.routeservice.model.*;
import ru.ityce4ka.routeservice.repository.ClustersRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteService {

    @Autowired
    ClusterizationService clusterizationService;

    @Autowired
    ClustersRepository clustersRepository;

    /**
     *
     * @param clusterRadius
     * @param countClusters
     * @param maxClusterSize
     * @return
     */
    public Optional<List<ClusterModel>>minRouteAlgorithm(double clusterRadius, int countClusters, int maxClusterSize){
        Pair<List<Cluster>,List<StoreModel>> clusterized = clusterizationService.minSizeAlgorithm(clusterRadius, countClusters, maxClusterSize);
        List<Cluster> clusters = clusterized.getFirst();
        List<StoreModel> stores = clusterized.getSecond();
        for(Cluster cluster : clusters){
            double[][] distanceMatrix = clusterizationService.getDistanceMatrix(cluster.getPoints());
            List<EdgeDistance> maxDistance = getMaxDistance(distanceMatrix);
            Point startPoint = cluster.getPoints().get(maxDistance.get(0).getId());
            if(startPoint != cluster.getPoints().get(0)){
                Point tempPoint = cluster.getPoints().get(0);
                cluster.getPoints().remove(startPoint);
                cluster.getPoints().set(0, startPoint);
                cluster.getPoints().add(tempPoint);
            }
            performInitCluster(cluster);
        }
        List<ClusterModel> clusterModels = castPointToStore(clusters,stores);
        clustersRepository.deleteAll();
        clustersRepository.saveAll(clusterModels);
        clusterModels = clustersRepository.findAll();
        return Optional.of(clusterModels);
    }

    /**
     *
     * @param clusterRadius
     * @param countClusters
     * @param maxClusterSize
     * @return
     */
    public Optional<List<ClusterModel>>minEdgesRouteAlgorithm(double clusterRadius, int countClusters, int maxClusterSize){
        Pair<List<Cluster>,List<StoreModel>> clusterized = clusterizationService.minEdgesAlgorithm(clusterRadius, countClusters, maxClusterSize);
        List<Cluster> clusters = clusterized.getFirst();
        List<StoreModel> stores = clusterized.getSecond();
        for(Cluster cluster : clusters){
            double[][] distanceMatrix = clusterizationService.getDistanceMatrix(cluster.getPoints());
            List<EdgeDistance> maxDistance = getMaxDistance(distanceMatrix);
            Point startPoint = cluster.getPoints().get(maxDistance.get(0).getId());
            if(startPoint != cluster.getPoints().get(0)){
                Point tempPoint = cluster.getPoints().get(0);
                cluster.getPoints().remove(startPoint);
                cluster.getPoints().set(0, startPoint);
                cluster.getPoints().add(tempPoint);
            }
            performInitCluster(cluster);
        }
        List<ClusterModel> clusterModels = castPointToStore(clusters,stores);
        clustersRepository.deleteAll();
        clustersRepository.saveAll(clusterModels);
        clusterModels = clustersRepository.findAll();
        return Optional.of(clusterModels);
    }

    /**
     * Задание ТТ веса в зависимости от их удаленности.
     * @param distanceMatrix - матрица растояний между всеми ТТ в кластере
     * @return - Список ТТ отсортированый в зависиости от их веса.
     */
    List<EdgeDistance> getMaxDistance(double[][] distanceMatrix){
        ArrayList<EdgeDistance> distanceArray = new ArrayList<>();
        for(int i = 0; i < distanceMatrix.length; i++){
            double distance=0.0;
            for(int j = 0; j < distanceMatrix[i].length; j++){
                distance += distanceMatrix[i][j];
            }
            distanceArray.add(new EdgeDistance(i,distance));
        }
        distanceArray = (ArrayList<EdgeDistance>) distanceArray.stream()
                .sorted(Comparator.comparing(
                        EdgeDistance::getDistance,
                        Comparator.reverseOrder()))
                .collect(Collectors.toList());
        return distanceArray;
    }

    /**
     *Поиск точки с минимальным ребром
     * @param startPoint - начальная точка
     * @param matrixDistance - матрица дистанций между ТТ в кластере
     * @return - индекс конечной точки
     */
    int getMinDistancePoint(int startPoint, double[][] matrixDistance){
        int point=0;
        double minDistance = Double.MAX_VALUE;
        for(int i = 1; i < matrixDistance[startPoint].length; i++){
            if(matrixDistance[startPoint][i]<minDistance&&i>startPoint){
                minDistance = matrixDistance[startPoint][i];
                point = i;
            }
        }
        return point;
    }

    /**
     *Сортировка точек по минимальным ребрам в кластере
     * @param cluster - кластер для сортировки
     */
    void performInitCluster(Cluster cluster){
        for(int i = 0; i<cluster.getPoints().size()-2;i++){
            int endPoint = getMinDistancePoint(i,clusterizationService.getDistanceMatrix(cluster.getPoints()));
            if(endPoint != i+1) {
                Point tempPointA = cluster.getPoints().get(endPoint);
                Point tempPointB = cluster.getPoints().get(i+1);
                cluster.getPoints().remove(endPoint);
                cluster.getPoints().set(i+1, tempPointA);
                cluster.getPoints().add(tempPointB);
            }
        }
    }

    /**
     * Преобразование списка кластеров в список ТТ разбитый по кластерам
     * @param clusters - список кластеров
     * @param stores - список всех ТТ
     * @return - список ТТ разбитый по кластерам
     */
    List<ClusterModel> castPointToStore(List<Cluster> clusters, List<StoreModel> stores){
        ArrayList<ClusterModel> result = new ArrayList<>();
        for(Cluster cluster : clusters){
            ClusterModel clusterModel = new ClusterModel();
            clusterModel.setId(clusters.indexOf(cluster));
            for(Point point : cluster.getPoints()){
                clusterModel.getStores().add(stores.get(point.getId()));
            }
            result.add(clusterModel);
        }
        return result;
    }

}
