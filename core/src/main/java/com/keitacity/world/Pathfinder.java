package com.keitacity.world;

import java.util.*;

public class Pathfinder {

    private static class Node {
        int x;
        int y;
        int cost;
        int priority;
        Node parent;

        Node(int x, int y, int cost, int priority, Node parent) {
            this.x = x;
            this.y = y;
            this.cost = cost;
            this.priority = priority;
            this.parent = parent;
        }
    }

    public static List<int[]> findPath(
            World world,
            int startX,
            int startY,
            int targetX,
            int targetY) {

        // A busca A* usa a distancia Manhattan como heuristica, adequada para
        // movimentos em quatro direcoes e sem diagonais neste mapa.

        PriorityQueue<Node> open = new PriorityQueue<>(
                Comparator.comparingInt(n -> n.priority));

        Set<String> visited = new HashSet<>();

        open.add(new Node(
                startX,
                startY,
                0,
                distance(startX, startY, targetX, targetY),
                null));

        int[] dx = { 1, -1, 0, 0 };
        int[] dy = { 0, 0, 1, -1 };

        while (!open.isEmpty()) {

            Node current = open.poll();
            String key = current.x + "," + current.y;

            if (visited.contains(key))
                continue;
            visited.add(key);

            if (current.x == targetX && current.y == targetY) {
                return buildPath(current);
            }

            for (int i = 0; i < 4; i++) {

                int nextX = current.x + dx[i];
                int nextY = current.y + dy[i];

                if (!world.isWalkable(nextX, nextY)
                        && !(nextX == targetX && nextY == targetY)) {
                    continue;
                }

                String nextKey = nextX + "," + nextY;

                if (visited.contains(nextKey))
                    continue;

                int cost = current.cost + 5;

                if (world.grid.tiles[nextX][nextY].isRoad()) {
                    cost = current.cost + 1;
                }

                int priority = cost +
                        distance(nextX, nextY, targetX, targetY);

                open.add(new Node(
                        nextX,
                        nextY,
                        cost,
                        priority,
                        current));
            }
        }

        return Collections.emptyList();
    }

    private static int distance(
            int x1,
            int y1,
            int x2,
            int y2) {

        return Math.abs(x1 - x2) +
                Math.abs(y1 - y2);
    }

    private static List<int[]> buildPath(Node node) {

        List<int[]> path = new ArrayList<>();

        while (node != null) {
            path.add(new int[] { node.x, node.y });
            node = node.parent;
        }

        Collections.reverse(path);

        if (!path.isEmpty()) {
            path.remove(0);
        }

        return path;
    }
}