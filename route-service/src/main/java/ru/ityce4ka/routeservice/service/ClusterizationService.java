package ru.ityce4ka.routeservice.service;


import lombok.extern.slf4j.Slf4j;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.ityce4ka.routeservice.model.*;
import ru.ityce4ka.routeservice.repository.ClustersRepository;
import ru.ityce4ka.routeservice.repository.StoreRepository;


import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClusterizationService {

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    ClustersRepository clustersRepository;

    /**
     * Получить все кластеры.
     * @return - список кластеров.
     */
    public Optional<List<ClusterModel>> findAll(){
        return Optional.of(
                clustersRepository.findAll()
        );
    }

    /**
     * получить кластер по ID.
     * @param id - ID кластера.
     * @return - кластер.
     */
    public Optional<ClusterModel> findById(Long id){
        return clustersRepository.findById(id);
    }

    /**
     * Алгоритм кластеризации по минимальным ребрам.
     * Сортирует ребра между точками в порядке возрастания.
     * Обьявляет все точки кластерами.
     * Проходит по каждому ребру и пытается объеденить кластеры.
     * Кластеры считаются объедененными если максимальное ребро, в случае объединения, короче чем радиус * 2
     * Условием окончания считается достижение необходимого количества кластеров, или закончились ребра.
     * @param clusterRadius - максимальный радиус кластера.
     * @param countClusters - нужное количество кластеров.
     * @param maxSize - максимальное количество точек в кластере.
     * @return - пара из списков кластеров и магазинов.
     */
    public Pair<List<Cluster>, List<StoreModel>> minEdgesAlgorithm(double clusterRadius, int countClusters, int maxSize){
        List<StoreModel> stores = storeRepository.findAll();
        List<Point> points = castStoresToPoints(stores);
        double[][] matrixDistance = getDistanceMatrix(points);
        List<Edge> edges = calculateEdges(matrixDistance);
        edges = edges.stream().sorted(Comparator.comparing(Edge::getDistance)).collect(Collectors.toList());
        ArrayList<Cluster> clusters = new ArrayList<>(points.size());
        for(Point point: points){
            Cluster cluster = new Cluster();
            ArrayList<Point> clusterPoints = new ArrayList<>();
            clusterPoints.add(point);
            cluster.setPoints(clusterPoints);
            clusters.add(cluster);
        }
        if(clusters.size() <= countClusters) return Pair.of(clusters, stores);
        for(Edge edge: edges){
            int clusterIdA = findClusterByPoint(clusters, points.get(edge.getIdA()));
            int clusterIdB = findClusterByPoint(clusters, points.get(edge.getIdB()));
            if(clusterIdA == clusterIdB){
                continue;
            }

            boolean canUnion = canClusterUnion(clusters.get(clusterIdA), clusters.get(clusterIdB), matrixDistance, clusterRadius, maxSize);
            if(canUnion) {
                unionClusters(clusters, clusters.get(clusterIdA), clusters.get(clusterIdB));
                if(clusters.size()==countClusters){
                    break;
                }
            }
        }
        return Pair.of(clusters, stores);
    }

    /**
     * Алгоритм кластеризации по минимальному размеру кластера.
     * Сортируются кластеры по количеству ТТ.
     * Пытаемся объединить кластер с минимальным количеством точек с кластером по минимальному ребру.
     * Кластеры считаются объедененными если максимальное ребро, в случае объединения, короче чем радиус * 2
     * Условием окончания считается достижение необходимого количества кластеров, или не было объединений за проход.
     * @param clusterRadius - максимальный радиус кластера.
     * @param countClusters - нужное количество кластеров.
     * @param maxSize - максимальное количество точек в кластере.
     * @return - пара из списков кластеров и магазинов.
     */
    public Pair<List<Cluster>, List<StoreModel>> minSizeAlgorithm(double clusterRadius, int countClusters, int maxSize){
        List<StoreModel> stores = storeRepository.findAll();
        List<Point> points = castStoresToPoints(stores);
        double[][] matrixDistance = getDistanceMatrix(points);
        List<Edge> edges = calculateEdges(matrixDistance);
        edges = edges.stream().sorted(Comparator.comparing(Edge::getDistance)).collect(Collectors.toList());
        ArrayList<Cluster> clusters = new ArrayList<>(points.size());
        for(Point point: points){
            Cluster cluster = new Cluster();
            ArrayList<Point> clusterPoints = new ArrayList<>();
            clusterPoints.add(point);
            cluster.setPoints(clusterPoints);
            clusters.add(cluster);
        }
        if(clusters.size() <= countClusters) return Pair.of(clusters, stores);
        while (true){
            boolean wasChangesFlag = false;
            clusters = (ArrayList<Cluster>) clusters.stream()
                    .sorted(Comparator.comparingInt(c -> c.getPoints().size()))
                    .collect(Collectors.toList());
            clusterloop:
            for(int clusterIdA = 0; clusterIdA<clusters.size(); clusterIdA++){
                ArrayList<Cluster> finalClusters = clusters;
                int finalClusterIdA = clusterIdA;
                List<Edge> edgesA = edges.stream()
                        .filter(e -> finalClusters
                                .get(finalClusterIdA)
                                .getPoints().stream()
                                .anyMatch(p -> e.getIdA() == p.getId()))
                        .collect(Collectors.toList());
                for(Edge edge: edgesA) {
                    int clusterIdB = findClusterByPoint(clusters, points.get(edge.getIdB()));
                    if (clusterIdA == clusterIdB) {
                        continue;
                    }
                    boolean canUnion = canClusterUnion(clusters.get(clusterIdA), clusters.get(clusterIdB), matrixDistance, clusterRadius, maxSize);
                    if (canUnion) {
                        wasChangesFlag = true;
                        unionClusters(clusters, clusters.get(clusterIdA), clusters.get(clusterIdB));
                        break clusterloop;
                    }
                }

            }
            if(clusters.size() == countClusters || !wasChangesFlag){
                break;
            }
        }
        return Pair.of(clusters, stores);

    }

    /**
     * Объединение двух кластеров.
     * @param clusters - список всех кластеров
     * @param clusterA - Куда
     * @param clusterB - Откуда
     */
    void unionClusters(List<Cluster> clusters, Cluster clusterA, Cluster clusterB){
        clusterA.getPoints().addAll(clusterB.getPoints());
        clusters.remove(clusterB);
        return;
    }

    /**
     * Поиск кластера по ТТ.
     * @param clusters - список кластеров
     * @param point - ТТ.
     * @return - индекс кластера в clusters
     */
    int findClusterByPoint(List<Cluster> clusters, Point point){
        for(Cluster cluster: clusters){
            for(Point p: cluster.getPoints()){
                if(point.getId() == p.getId()){
                    return clusters.indexOf(cluster);
                }
            }
        }
        return -1;
    }

    /**
     * Проверка кластеров на возможность объединения.
     * @param clusterA - Первый кластер
     * @param clusterB - Второй кластер
     * @param matrixDistance - матрица расстояний между ТТ
     * @param clusterRadius - радиус кластера
     * @param maxSize - максимальный размер кластера
     * @return - True если можно объединить кластеры, иначе False
     */
    boolean canClusterUnion(Cluster clusterA, Cluster clusterB, double[][] matrixDistance, double clusterRadius, int maxSize){
        if(clusterA.getPoints().size() + clusterB.getPoints().size() >= maxSize) return false;
        for(Point pointA: clusterA.getPoints()){
            for(Point pointB: clusterB.getPoints()){
                double distance = 0.0;
                if(pointA.getId()<pointB.getId()) {
                     distance = matrixDistance[pointA.getId()][pointB.getId()];
                }else {
                     distance = matrixDistance[pointB.getId()][pointA.getId()];
                }
                if (distance > clusterRadius * 2) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Получение матрицы расстояний ТТ
     * @param points - список ТТ
     * @return - матрица растояний между ТТ
     */
    public double [][] getDistanceMatrix(List<Point> points) {
        double[][] matrixDistance = new double[points.size()][points.size()];
        for(int i =0; i<points.size(); i++) {
            for (int j = 0; j < points.size(); j++) {
                matrixDistance[i][j] = distance(points.get(i), points.get(j));
            }
        }
        return matrixDistance;
    }

    /**
     * Удаление ненужных полей из ТТ
     * @param stores -список ТТ
     * @return - оптимизированый список ТТ
     */
    List<Point> castStoresToPoints(List<StoreModel> stores){
        ArrayList<Point> points = new ArrayList<>(stores.size());
        for(StoreModel store : stores){
            points.add(new Point(stores.indexOf(store), store.getLongitude(), store.getLatitude()));
        }
        return points;
    }

    /**
     * Преобразование матрицы расстояний в список ребер.
     * @param distanceMatrix -матрица расстояний между ТТ
     * @return - список ребер
     */
    List<Edge> calculateEdges(double[][] distanceMatrix){
        ArrayList<Edge> edges = new ArrayList<>();
        for(int i = 0; i<distanceMatrix.length; i++){
            for(int j = i+1; j<distanceMatrix.length; j++){
                edges.add(new Edge(i, j, distanceMatrix[i][j]));
            }
        }
        return edges;
    }

    /**
     *Вычесление расстояния между двумя ТТ в метрах по координатам
     * @param point1 - начальная ТТ
     * @param point2 - конечная ТТ
     * @return - растояние между ТТ в метрах
     */
    double distance(Point point1, Point point2){
        GeodeticCalculator geoCalc = new GeodeticCalculator();

        Ellipsoid reference = Ellipsoid.WGS84;

        GlobalPosition pointA = new GlobalPosition(point1.getLatitude(), point1.getLongitude(), 0.0); // Point A

        GlobalPosition userPos = new GlobalPosition(point2.getLatitude(), point2.getLongitude(), 0.0); // Point B

        return geoCalc.calculateGeodeticCurve(reference, userPos, pointA).getEllipsoidalDistance();
    }
}
