package com.delivery.delivery_app.utils;

import java.util.ArrayList;
import java.util.List;

public class Node {
    int id;
    double latitude;
    double longitude;
    double fScore;
    List<Edge> edges;

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getfScore() {
        return fScore;
    }

    public void setfScore(double fScore) {
        this.fScore = fScore;
    }

    public Node(int id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.edges = new ArrayList<>();
        this.fScore = Double.POSITIVE_INFINITY;
    }

    public void addEdge(Node target, double cost) {
        edges.add(new Edge(this, target, cost));
    }

}

