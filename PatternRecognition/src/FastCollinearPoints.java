import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {
    public FastCollinearPoints(Point[] points) {
        if(points == null) {
            throw new IllegalArgumentException();
        }

        _startPoints = new ArrayList<>();
        _finishPoints = new ArrayList<>();

        if(points.length < 4) {
            _segments = new LineSegment[0];
            return;
        }

        ArrayList<LineSegment> segments = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            Arrays.sort(points);
            swap(points, i, 0);
            Point targetPoint = points[0];
            Arrays.sort(points, 1, points.length, targetPoint.slopeOrder());

            double previousSlopeToTarget = points[1].slopeTo(targetPoint);
            int startIndexOfEquals = 1;
            int finishIndexOfEquals = 1;

            for(int j = 2; j < points.length; j++) {
                double currentSlope = points[j].slopeTo(targetPoint);
                if(currentSlope == previousSlopeToTarget) {
                    finishIndexOfEquals++;
                }
                else {
                    addToSegmentsIfNotExists(segments, points, startIndexOfEquals, finishIndexOfEquals);

                    startIndexOfEquals = j;
                    finishIndexOfEquals = j;
                    previousSlopeToTarget = currentSlope;
                }
            }

            addToSegmentsIfNotExists(segments, points, startIndexOfEquals, finishIndexOfEquals);
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
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

    private void addToSegmentsIfNotExists(ArrayList<LineSegment> segments, Point[] points, int start, int finish) {
        if(finish - start < 2) {
            return;
        }

        Point minPoint = findMinPoint(points, points[0], start, finish);
        Point maxPoint = findMaxPoint(points, points[0], start, finish);

        if(!isSegmentExists(minPoint, maxPoint)) {
            _finishPoints.add(maxPoint);
            _startPoints.add(minPoint);
            segments.add(new LineSegment(minPoint, maxPoint));
        }
    }

    private boolean isSegmentExists(Point minPoint, Point maxPoint) {
        for(int i = 0; i < _startPoints.size(); i++) {
            if(_startPoints.get(i).compareTo(minPoint) == 0 && _finishPoints.get(i).compareTo(maxPoint) == 0){
                return true;
            }
        }

        return false;
    }

    private static void swap(Point[] points, int i, int j) {
        Point tmp = points[i];
        points[i] = points[j];
        points[j] = tmp;
    }

    private static Point findMaxPoint(Point[] points, Point additional, int start, int finish) {
        Point maxPoint = additional;

        for(int j = start; j <= finish; j++){
            if(points[j].compareTo(maxPoint) > 0) {
                maxPoint = points[j];
            }
        }

        return maxPoint;
    }

    private Point findMinPoint(Point[] points, Point additional, int start, int finish) {
        Point minPoint = additional;

        for(int j = start; j <= finish; j++){
            if(points[j].compareTo(minPoint) < 0) {
                minPoint = points[j];
            }
        }

        return minPoint;
    }

    private final LineSegment[] _segments;
    private final ArrayList<Point> _startPoints;
    private final ArrayList<Point> _finishPoints;
}
