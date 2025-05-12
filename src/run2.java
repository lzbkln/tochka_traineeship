import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class run2 {

    private static final char EMPTY_CELL = '.';
    private static final char WALL = '#';
    private static final char ROBOT = '@';

    enum Direction {
        UP(-1, 0),
        LEFT(0, -1),
        DOWN(1, 0),
        RIGHT(0, 1);

        private final int rowDelta;
        private final int colDelta;

        Direction(int rowDelta, int colDelta) {
            this.rowDelta = rowDelta;
            this.colDelta = colDelta;
        }

        public int getRow() {
            return rowDelta;
        }

        public int getCol() {
            return colDelta;
        }
    }

    private static char[][] getInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> lines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }

        char[][] data = new char[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            data[i] = lines.get(i).toCharArray();
        }

        return data;
    }

    static class Node implements Comparable<Node> {
        int distance;
        String[] robotPositions;
        int keys;

        Node(int distance, String[] robotPositions, int keys) {
            this.distance = distance;
            this.robotPositions = robotPositions;
            this.keys = keys;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    private static int solve(char[][] data) {
        int rows = data.length, cols = data[0].length;
        Map<String, int[]> positions = new HashMap<>();
        int keyCount = 0;
        int keyMask = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char cell = data[r][c];
                if (cell != EMPTY_CELL && cell != WALL) {
                    if (cell == ROBOT) {
                        String robotId = String.valueOf(keyCount);
                        data[r][c] = robotId.charAt(0);
                        positions.put(robotId, new int[]{r, c});
                        keyCount++;
                    } else if (Character.isLowerCase(cell)) {
                        keyMask = keyMask | (1 << (cell - 'a'));
                        positions.put(String.valueOf(cell), new int[]{r, c});
                    } else {
                        positions.put(String.valueOf(cell), new int[]{r, c});
                    }
                }
            }
        }

        String[] robots = new String[keyCount];
        for (int i = 0; i < keyCount; i++) {
            robots[i] = String.valueOf(i);
        }

        Map<String, Map<String, Integer>> distances = new HashMap<>();
        for (String position : positions.keySet()) {
            distances.put(position, bfs(position, positions, data, rows, cols));
        }

        return dijkstra(distances, robots, keyMask);
    }

    private static int dijkstra(Map<String, Map<String, Integer>> distances, String[] robots, int targetKeyMask) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.offer(new Node(0, robots, 0));

        Map<String, Integer> minDist = new HashMap<>();
        String startState = String.join(",", robots) + "#" + 0;
        minDist.put(startState, 0);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            int currentDistance = current.distance;
            String[] currentRobotPositions = current.robotPositions;
            int currentKeyMask = current.keys;

            String currentId = String.join(",", currentRobotPositions) + "#" + currentKeyMask;

            if (minDist.getOrDefault(currentId, Integer.MAX_VALUE) < currentDistance) continue;
            if (currentKeyMask == targetKeyMask) return currentDistance;

            for (int i = 0; i < currentRobotPositions.length; i++) {
                String robotPos = currentRobotPositions[i];
                Map<String, Integer> possibleMoves = distances.get(robotPos);
                if (possibleMoves == null) continue;

                for (Map.Entry<String, Integer> entry : possibleMoves.entrySet()) {
                    String target = entry.getKey();
                    int moveDistance = entry.getValue();

                    int newKeyMask = currentKeyMask;
                    char targetChar = target.charAt(0);
                    if (Character.isLowerCase(targetChar)) {
                        newKeyMask = newKeyMask | (1 << (targetChar - 'a'));
                    } else if (Character.isUpperCase(targetChar)) {
                        if ((currentKeyMask & (1 << (targetChar - 'A'))) == 0) {
                            continue;
                        }
                    }

                    String[] newPositions = Arrays.copyOf(currentRobotPositions, currentRobotPositions.length);
                    newPositions[i] = target;

                    String newId = String.join(",", newPositions) + "#" + newKeyMask;

                    if (currentDistance + moveDistance < minDist.getOrDefault(newId, Integer.MAX_VALUE)) {
                        minDist.put(newId, currentDistance + moveDistance);
                        queue.offer(new Node(currentDistance + moveDistance, newPositions, newKeyMask));
                    }
                }
            }
        }

        return Integer.MAX_VALUE;
    }

    private static Map<String, Integer> bfs(String start, Map<String, int[]> positions, char[][] data, int rows, int cols) {
        int[] startPos = positions.get(start);
        int startRow = startPos[0];
        int startCol = startPos[1];

        boolean[][] visited = new boolean[rows][cols];
        visited[startRow][startCol] = true;

        Queue<int[]> bfsQueue = new LinkedList<>();
        bfsQueue.offer(new int[]{startRow, startCol, 0});

        Map<String, Integer> distanceMap = new HashMap<>();

        while (!bfsQueue.isEmpty()) {
            int[] curr = bfsQueue.poll();
            startRow = curr[0];
            startCol = curr[1];
            int distance = curr[2];

            String currentCell = String.valueOf(data[startRow][startCol]);
            if (!start.equals(currentCell) && !currentCell.equals(String.valueOf(EMPTY_CELL))) {
                distanceMap.put(currentCell, distance);
                continue;
            }

            for (Direction dir : Direction.values()) {
                int newRow = startRow + dir.getRow(), newCol = startCol + dir.getCol();
                if (0 <= newRow && newRow < rows && 0 <= newCol && newCol < cols) {
                    if (data[newRow][newCol] != WALL && !visited[newRow][newCol]) {
                        visited[newRow][newCol] = true;
                        bfsQueue.offer(new int[]{newRow, newCol, distance + 1});
                    }
                }
            }
        }

        return distanceMap;
    }

    public static void main(String[] args) throws IOException {
        char[][] data = getInput();
        int result = solve(data);

        if (result == Integer.MAX_VALUE) {
            System.out.println("No solution found");
        } else {
            System.out.println(result);
        }
    }
}
