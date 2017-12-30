import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.TreeSet;

public class PointSET {
    public PointSET()                               // construct an empty set of points
    {
        _points = new TreeSet<>();
    }

    public boolean isEmpty()                      // is the set empty?
    {
        return size() == 0;
    }

    public int size()                         // number of points in the set
    {
        return _points.size();
    }

    public void insert(Point2D p)              // add the point to the set (if it is not already in the set)
    {
        _points.add(p);
    }

    public boolean contains(Point2D p)            // does the set contain point p?
    {
        return _points.contains(p);
    }

    public void draw()                         // draw all points to standard draw
    {
        for (Point2D point : _points) {
            point.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect)             // all points that are inside the rectangle (or on the boundary)
    {
        ArrayList<Point2D> points = new ArrayList<>();
        for (Point2D point : _points) {
            if(point.x() >= rect.xmin() && point.x() <= rect.xmax() && point.y() >= rect.ymin() && point.y() <= rect.ymax()) {
                points.add(point);
            }
        }

        return points;
    }

    public Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty
    {
        Point2D nearest = _points.first();
        for (Point2D point : _points) {
            if(p.distanceSquaredTo(point) < p.distanceSquaredTo(nearest)) {
                nearest = point;
            }
        }

        return nearest;
    }

    public static void main(String[] args)          // unit testing of the methods (optional)
    {
        PointSET set = new PointSET();

        AssertTrue(set.isEmpty());
        AssertTrue(set.size() == 0);
        Point2D p = new Point2D(0, 0);
        set.insert(p);
        AssertTrue(!set.isEmpty());
        AssertTrue(set.size() == 1);
        AssertTrue(set.contains(p));
    }

    private static void AssertTrue(boolean flag) {
        if(!flag) {
        }
    }

    private final TreeSet<Point2D> _points;
}
