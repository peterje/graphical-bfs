package com.edmopet;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    static final int WIDTH = 480;
    static final int HEIGHT = 480;
    static final int NUMROWS = 16;
    static final int GAPSIZE = WIDTH / NUMROWS;
    static Set<Point> walls;
    public static void main(String[] args) throws FileNotFoundException {
        walls = new HashSet<>();
        Scanner input = new Scanner(new File("walls.txt"));
        while(input.hasNextLine()){
            int x = input.nextInt();
            int y = input.nextInt();
            walls.add(new Point(x, y));
        }
        Point startPoint = new Point(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
        Point endPoint = new Point(Integer.parseInt(args[2]),Integer.parseInt(args[3]));
        DrawingPanel dp = new DrawingPanel(WIDTH,HEIGHT);
        Graphics g = dp.getGraphics();
        for (Point wall : walls){
            rectAtPoint(wall, g, Color.BLACK);
        }
        initializeGrid(g);
        ArrayList<Point> path = drawBFS(startPoint, endPoint, g, dp);
        tracePath(path, g, dp);
        System.out.println("The shortest path between the two points is of length: " + path.size());
    }
    
    private static void tracePath(ArrayList<Point> path, Graphics g, DrawingPanel p) {
        for(Point pt : path){
            rectAtPoint(pt, g, Color.BLUE);
            p.sleep(25);
        }
    }

    static void rectAtPoint(Point p, Graphics g, Color c){
        g.setColor(c);
        int offset = 4;
        g.fillRect(p.x * GAPSIZE + offset, p.y * GAPSIZE + offset, GAPSIZE - offset*2, GAPSIZE - offset*2);
        g.setColor(Color.BLACK);
    }
    public static void initializeGrid(Graphics g){
        for(int i = 0; i<NUMROWS; i++){
            g.drawLine(0, i*GAPSIZE, WIDTH, i*GAPSIZE);
        }

        for(int i = 0; i<NUMROWS; i++){
            g.drawLine(i*GAPSIZE, 0, i*GAPSIZE,HEIGHT );
        }
    }

    public static ArrayList<Point> drawBFS(Point start, Point end, Graphics g, DrawingPanel p){
        int[] dir = new int[]{-1,0, 1};
        boolean[][] visited = new boolean[NUMROWS][NUMROWS];
        LinkedList<ArrayList<Point>> queue = new LinkedList<>();
        ArrayList<Point> initialPath = new ArrayList<>();
        initialPath.add(start);
        queue.add(initialPath);
        visited[start.x][start.y] = true;
        while(!queue.isEmpty()){
            ArrayList<Point> path = queue.poll(); // get the next path in the queue
            Point lastNode = path.get(path.size()-1); /// get the last node in the path
            for(int dx : dir){
                for(int dy : dir){
                    Point adjacent = new Point(lastNode.x + dx, lastNode.y + dy);
                    if(isValid(adjacent) && !visited[adjacent.x][adjacent.y]){
                        visited[adjacent.x][adjacent.y] = true;
                        if(adjacent.x == end.x && adjacent.y == end.y) { // we found a path to the end
                            rectAtPoint(end, g, Color.CYAN);
                            return path;
                        }
                        ArrayList<Point> newPath = new ArrayList<>(path);
                        newPath.add(adjacent);
                        queue.add(newPath);
                        rectAtPoint(adjacent, g, Color.RED);
                        initializeGrid(g);
                        p.sleep(50);
                    }
                }
            }
        }
        return null;
    }
    static boolean isValid(Point p){
        boolean isFreeSpace = true;
        for(Point wall : walls){
            if(p.x == wall.x && p.y == wall.y){
                isFreeSpace = false;
            }
        }
        return isFreeSpace && p.x < NUMROWS && p.x >= 0 && p.y < NUMROWS && p.y >= 0;
    }
}

class Point {
    public int x, y;
    boolean visited;
    public Point(int x, int y){
        this.x = x;
        this.y = y;
        this.visited = false;
    }

    public boolean equals(Point obj) {
        return this.x == obj.x && this.y == obj.y;
    }
}
