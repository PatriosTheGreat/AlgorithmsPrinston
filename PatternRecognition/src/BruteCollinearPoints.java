import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class BruteCollinearPoints {
    public BruteCollinearPoints(Point[] points) {
        if(points == null) {
            throw new IllegalArgumentException();
        }

        ArrayList<LineSegment> segments = new ArrayList<>();
        for(int p = 0; p < points.length; p++) {
            for(int q = p + 1; q < points.length; q++) {
                for(int r = q + 1; r < points.length; r++) {
                    for(int s = r + 1; s < points.length; s++) {
                        double pToQ = points[p].slopeTo(points[q]);
                        double qToR = points[q].slopeTo(points[r]);
                        double rToS = points[r].slopeTo(points[s]);
                        if(pToQ == qToR && qToR == rToS) {
                            Point maxPoint = findMaxPoint(points[p], points[q], points[r], points[s]);
                            Point minPoint = findMinPoint(points[p], points[q], points[r], points[s]);
                            segments.add(new LineSegment(minPoint, maxPoint));
                        }
                    }
                }
            }
        }

        _segments = segments.toArray(new LineSegment[segments.size()]);
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

    public int numberOfSegments() {
        return _segments.length;
    }

    public LineSegment[] segments() {
        return _segments;
    }

    private LineSegment[] _segments;


    private static Point findMaxPoint(Point point1, Point point2, Point point3, Point point4) {
        if(point1.compareTo(point2) > 0 && point1.compareTo(point3) > 0 && point1.compareTo(point4) > 0) {
            return point1;
        }

        if(point2.compareTo(point1) > 0 && point2.compareTo(point3) > 0 && point2.compareTo(point4) > 0) {
            return point2;
        }

        if(point3.compareTo(point1) > 0 && point3.compareTo(point2) > 0 && point3.compareTo(point4) > 0) {
            return point3;
        }

        if(point4.compareTo(point1) > 0 && point4.compareTo(point2) > 0 && point4.compareTo(point1) > 0) {
            return point4;
        }

        return point1;
    }

    private Point findMinPoint(Point point1, Point point2, Point point3, Point point4) {
        if(point1.compareTo(point2) < 0 && point1.compareTo(point3) < 0 && point1.compareTo(point4) < 0) {
            return point1;
        }

        if(point2.compareTo(point1) < 0 && point2.compareTo(point3) < 0 && point2.compareTo(point4) < 0) {
            return point2;
        }

        if(point3.compareTo(point1) < 0 && point3.compareTo(point2) < 0 && point3.compareTo(point4) < 0) {
            return point3;
        }

        if(point4.compareTo(point1) < 0 && point4.compareTo(point2) < 0 && point4.compareTo(point1) < 0) {
            return point4;
        }

        return point1;
    }
}
