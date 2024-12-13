package com.delivery.delivery_app.service;

import com.delivery.delivery_app.constant.OrderType;
import com.delivery.delivery_app.constant.ProductSize;
import com.delivery.delivery_app.dto.route.Node;
import com.delivery.delivery_app.dto.route.Route;
import com.delivery.delivery_app.dto.route.RouteFinderRequest;
import com.delivery.delivery_app.dto.route.RouteResponse;
import com.delivery.delivery_app.mapper.NodeMapper;
import com.delivery.delivery_app.utils.Edge;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

@Service
//@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RouteService {
    Map<Integer, com.delivery.delivery_app.utils.Node> nodes;
    NodeMapper nodeMapper;

    @Autowired
    public RouteService(NodeMapper nodeMapper) {
        this.nodeMapper = nodeMapper;
        this.nodes = new HashMap<>();
        log.info("read files");
        loadDataSet();
    }

    private void loadDataSet() {
        try {
            ClassPathResource nodeData = new ClassPathResource("files/nodes.json");
            JSONArray nodesArray = new JSONArray(new String(FileCopyUtils.copyToByteArray(nodeData.getInputStream())));
            ClassPathResource edgeData = new ClassPathResource("files/edges.json");
            JSONArray edgesArray = new JSONArray(new String(FileCopyUtils.copyToByteArray(edgeData.getInputStream())));

            for (int i = 0; i < nodesArray.length(); i++) {
                JSONObject nodeObject = nodesArray.getJSONObject(i);
                int id = nodeObject.getInt("id");
                JSONObject data = nodeObject.getJSONObject("data");
                double latitude = data.getDouble("latitude");
                double longitude = data.getDouble("longitude");
                addNode(id, latitude, longitude);
            }

            for (int i = 0; i < edgesArray.length(); i++) {
                JSONObject edgeObject = edgesArray.getJSONObject(i);
                int from = edgeObject.getInt("fromId");
                int to = edgeObject.getInt("toId");
                double cost = edgeObject.getDouble("cost");
                addEdge(from, to, cost);
            }
        } catch (Exception e) {
            log.error("Error while reading files", e);
        }
    }

//    public RouteFinderResponse findRoute(String origin, String destination) {
//        String[] originData = origin.split(",");
//        String[] destinationData = destination.split(",");
//        double startLat = Double.parseDouble(originData[0]);
//        double startLang = Double.parseDouble(originData[1]);
//        double destinationLat = Double.parseDouble(destinationData[0]);
//        double destinationLang = Double.parseDouble(destinationData[1]);
//        Node start = getNearestNode(startLat, startLang);
//        Node goal = getNearestNode(destinationLat, destinationLang);
//        if (start == null || goal == null) return new RouteFinderResponse(Collections.emptyList());
//        List<NodeResponse> result = aStar(start.getId(), goal.getId());
//        result.addFirst(new NodeResponse(startLat, startLang));
//        result.add(new NodeResponse(destinationLat, destinationLang));
//        return new RouteFinderResponse(result);
//    }

    public RouteResponse findRoute(RouteFinderRequest request) {
        com.delivery.delivery_app.utils.Node start = getNearestNode(request.getOrigin().getLatitude(), request.getOrigin().getLongitude());
        com.delivery.delivery_app.utils.Node goal = getNearestNode(request.getDestination().getLatitude(), request.getDestination().getLongitude());
        if (start == null || goal == null) return new RouteResponse(Collections.emptyList(), 0.0, "", 0);

        double totalDistance = 0.0;
        List<Node> result = new LinkedList<>();
        result.add(request.getOrigin());

        if (request.getStops() == null || request.getStops().isEmpty()) {
            var route = aStar(start.getId(), goal.getId());
            totalDistance += route.getDistance();
            result.addAll(route.getNodes());
            result.add(request.getDestination());
        } else {
            for (Node stop : request.getStops()) {
                com.delivery.delivery_app.utils.Node stopNode = getNearestNode(stop.getLatitude(), stop.getLongitude());
                if (stopNode == null) return new RouteResponse(Collections.emptyList(), 0.0, "", 0);
                var routeResponse = aStar(start.getId(), stopNode.getId());
                totalDistance += routeResponse.getDistance();
                result.addAll(routeResponse.getNodes());
                result.add(stop);
                start = stopNode;
            }
            var routeResponse = aStar(start.getId(), goal.getId());
            totalDistance += routeResponse.getDistance();
            result.addAll(routeResponse.getNodes());
            result.add(request.getDestination());
        }
        
        return new RouteResponse(result, totalDistance, estimateTime(totalDistance), estimateCost(request.getOrderType(), request.getProductSize(), totalDistance));
    }

    private Route aStar(int startId, int goalId) {
        com.delivery.delivery_app.utils.Node start = nodes.get(startId);
        com.delivery.delivery_app.utils.Node goal = nodes.get(goalId);
        if (start == null || goal == null) return new Route(Collections.emptyList(), 0.0);

        PriorityQueue<com.delivery.delivery_app.utils.Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(com.delivery.delivery_app.utils.Node::getfScore));
        Set<com.delivery.delivery_app.utils.Node> closedSet = new HashSet<>();

        Map<com.delivery.delivery_app.utils.Node, com.delivery.delivery_app.utils.Node> cameFrom = new HashMap<>();
        Map<com.delivery.delivery_app.utils.Node, Double> gScore = new HashMap<>();
        Map<com.delivery.delivery_app.utils.Node, Double> fScore = new HashMap<>();

        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, goal));
        start.setfScore(fScore.get(start));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            com.delivery.delivery_app.utils.Node current = openSet.poll();

            if (current.equals(goal)) {
                List<Node> path = reconstructPath(cameFrom, current);
                double distance = gScore.get(current);
                return new Route(path, distance);
            }

            closedSet.add(current);

            for (Edge edge : current.getEdges()) {
                com.delivery.delivery_app.utils.Node neighbor = edge.getTo();
                if (closedSet.contains(neighbor)) continue;

                double tentativeGScore = gScore.get(current) + edge.getCost();

                if (!openSet.contains(neighbor) || tentativeGScore < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristic(neighbor, goal));
                    neighbor.setfScore(fScore.get(neighbor));

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return new Route(Collections.emptyList(), 0.0);
    }

    private List<Node> reconstructPath(Map<com.delivery.delivery_app.utils.Node, com.delivery.delivery_app.utils.Node> cameFrom, com.delivery.delivery_app.utils.Node current) {
        List<Node> path = new ArrayList<>();
        while (current != null) {
            path.add(nodeMapper.toNodeResponse(current));
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    private void addNode(int id, double lat, double lang) {
        nodes.put(id, new com.delivery.delivery_app.utils.Node(id, lat, lang));
    }

    private void addEdge(int fromId, int toId, double cost) {
        com.delivery.delivery_app.utils.Node from = nodes.get(fromId);
        com.delivery.delivery_app.utils.Node to = nodes.get(toId);
        if (from != null && to != null) {
            from.addEdge(to, cost);
        }
    }

    private double heuristic(com.delivery.delivery_app.utils.Node a, com.delivery.delivery_app.utils.Node b) {
        double latDistance = Math.toRadians(b.getLatitude() - a.getLatitude());
        double lngDistance = Math.toRadians(b.getLongitude() - a.getLongitude());
        double haversine = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(a.getLatitude())) * Math.cos(Math.toRadians(b.getLatitude()))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double result = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
        return 6371 * result;
    }

    private com.delivery.delivery_app.utils.Node getNearestNode(double lat, double lang) {
        com.delivery.delivery_app.utils.Node nearestNode = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (com.delivery.delivery_app.utils.Node node : nodes.values()) {
            double distance = heuristic(node, new com.delivery.delivery_app.utils.Node(-1, lat, lang));
            if (distance < minDistance) {
                minDistance = distance;
                nearestNode = node;
            }
        }
        return nearestNode;
    }

    private String estimateTime(double distance) {
        double slowSpeed = 30.0;
        double fastSpeed = 40.0;
        double slowTime = distance / slowSpeed;
        double fastTime = distance / fastSpeed;
        Integer slowTimeInMinutes = (int) (slowTime * 60);
        Integer fastTimeInMinutes = (int) (fastTime * 60);
        Integer random = new Random().nextInt(5);
        return (fastTimeInMinutes + random) + " - " + (slowTimeInMinutes + random)+ " phút";
    }

    public Integer estimateCost(OrderType orderType, ProductSize productSize, double distance) {
        if (distance <= 0) {
            return 0;
        }

        int totalPrice;

        switch (orderType) {
            case OrderType.RIDE:
                totalPrice = calculateRideCost(distance);
                break;

            case OrderType.FOOD_DELIVERY:
                totalPrice = calculateFoodDeliveryCost(distance);
                break;

            case OrderType.DELIVERY:
                totalPrice = calculateDeliveryCost(productSize, distance);
                break;

            default:
                throw new IllegalArgumentException("Invalid order type");
        }

        return totalPrice;
    }
    private int calculateRideCost(double distance) {
        if (distance <= 0) {
            return 0;
        }
        int firstKmPrice = 11000;
        int additionalKmPrice = 3800;
        int totalPrice;
        if (distance <= 1) {
            totalPrice = firstKmPrice;
        } else {
            totalPrice = firstKmPrice + (int) Math.ceil(distance - 1) * additionalKmPrice;
        }
        return totalPrice;
    }

    private int calculateFoodDeliveryCost(double distance) {
        int basePrice = 8000;
        int perKmPrice = 2500;
        if (distance <= 2) {
            return basePrice;
        } else {
            return basePrice + (int) Math.ceil(distance - 2) * perKmPrice;
        }
    }

    private int calculateDeliveryCost(ProductSize productSize, double distance) {
        int basePrice;
        int perKmPrice;

        switch (productSize) {
            case ProductSize.SMALL:
                basePrice = 10000;
                perKmPrice = 3000;
                break;

            case ProductSize.MEDIUM:
                basePrice = 15000;
                perKmPrice = 4000;
                break;

            case ProductSize.LARGE:
                basePrice = 25000;
                perKmPrice = 5000;
                break;
            default:
                basePrice = 10000;
                perKmPrice = 3000;
        }

        if (distance <= 2) {
            return basePrice;
        } else {
            return basePrice + (int) Math.ceil(distance - 2) * perKmPrice;
        }
    }


}
