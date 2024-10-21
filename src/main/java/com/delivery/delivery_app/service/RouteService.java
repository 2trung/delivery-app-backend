package com.delivery.delivery_app.service;

import com.delivery.delivery_app.dto.route.NodeResponse;
import com.delivery.delivery_app.dto.route.RouteFinderRequest;
import com.delivery.delivery_app.dto.route.RouteFinderResponse;
import com.delivery.delivery_app.mapper.NodeMapper;
import com.delivery.delivery_app.utils.Edge;
import com.delivery.delivery_app.utils.Node;
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
    Map<Integer, Node> nodes;
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

    public RouteFinderResponse findRoute(RouteFinderRequest request) {
        Node start = getNearestNode(request.getOrigin().getLatitude(), request.getOrigin().getLongitude());
        Node goal = getNearestNode(request.getDestination().getLatitude(), request.getDestination().getLongitude());
        if (start == null || goal == null) return new RouteFinderResponse(Collections.emptyList());
        if (request.getStops() == null || request.getStops().length == 0) {
            List<NodeResponse> result = aStar(start.getId(), goal.getId());
            result.addFirst(request.getOrigin());
            result.add(request.getDestination());
            return new RouteFinderResponse(result);
        } else {
            List<NodeResponse> result = new LinkedList<>();
            result.add(request.getOrigin());
            for (NodeResponse stop : request.getStops()) {
                Node stopNode = getNearestNode(stop.getLatitude(), stop.getLongitude());
                if (stopNode == null) return new RouteFinderResponse(Collections.emptyList());
                List<NodeResponse> path = aStar(start.getId(), stopNode.getId());
                result.addAll(path);
                result.add(stop);
                start = stopNode;
            }
            List<NodeResponse> path = aStar(start.getId(), goal.getId());
            result.addAll(path);
            result.add(request.getDestination());
            return new RouteFinderResponse(result);
        }

    }

    private List<NodeResponse> aStar(int startId, int goalId) {
        Node start = nodes.get(startId);
        Node goal = nodes.get(goalId);
        if (start == null || goal == null) return Collections.emptyList();

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getfScore));
        Set<Node> closedSet = new HashSet<>();

        Map<Node, Node> cameFrom = new HashMap<>();
        Map<Node, Double> gScore = new HashMap<>();
        Map<Node, Double> fScore = new HashMap<>();

        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, goal));
        start.setfScore(fScore.get(start));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            closedSet.add(current);

            for (Edge edge : current.getEdges()) {
                Node neighbor = edge.getTo();
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
        return Collections.emptyList();
    }

    private List<NodeResponse> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<NodeResponse> path = new ArrayList<>();
        while (current != null) {
            path.add(nodeMapper.toNodeResponse(current));
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    private void addNode(int id, double lat, double lang) {
        nodes.put(id, new Node(id, lat, lang));
    }

    private void addEdge(int fromId, int toId, double cost) {
        Node from = nodes.get(fromId);
        Node to = nodes.get(toId);
        if (from != null && to != null) {
            from.addEdge(to, cost);
        }
    }

    private double heuristic(Node a, Node b) {
        double latDistance = Math.toRadians(b.getLatitude() - a.getLatitude());
        double lngDistance = Math.toRadians(b.getLongitude() - a.getLongitude());
        double haversine = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(a.getLatitude())) * Math.cos(Math.toRadians(b.getLatitude()))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double result = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
        return 6371 * result;
    }

    private Node getNearestNode(double lat, double lang) {
        Node nearestNode = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Node node : nodes.values()) {
            double distance = heuristic(node, new Node(-1, lat, lang));
            if (distance < minDistance) {
                minDistance = distance;
                nearestNode = node;
            }
        }
        return nearestNode;
    }

}
