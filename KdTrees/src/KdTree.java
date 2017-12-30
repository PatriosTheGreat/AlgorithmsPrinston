import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;

public final class KdTree {
    public KdTree()                               // construct an empty set of points
    {
        _nodesCount = 0;
        _root = null;
    }

    public boolean isEmpty()                      // is the set empty?
    {
        return size() == 0;
    }

    public int size()                         // number of points in the set
    {
        return _nodesCount;
    }

    public void insert(Point2D p)              // add the point to the set (if it is not already in the set)
    {
        RectHV totalRect = new RectHV(0, 0, 1, 1);
        if(_nodesCount == 0) {
            _root = new KdTreeNode(p, totalRect);
            _nodesCount = 1;
        }
        else if(insert_vertical(_root, p, totalRect)) {
            _nodesCount++;
        }
    }

    public boolean contains(Point2D p)            // does the set contain point p?
    {
        if(_root == null) {
            return false;
        }

        return contains_vertical(_root, p);
    }

    public void draw()                         // draw all points to standard draw
    {
        if(_root == null) {
            return;
        }

        _root.draw(false);
    }

    public Iterable<Point2D> range(RectHV rect)             // all points that are inside the rectangle (or on the boundary)
    {
        ArrayList<Point2D> points = new ArrayList<>();
        if(_root != null) {
            range(rect, _root, points);
        }

        return points;
    }

    public Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty
    {
        if(_root == null) {
            return null;
        }

        return nearest(_root, p, _root.get_point());
    }

    public static void main(String[] args)                  // unit testing of the methods (optional)
    {
        /*
        KdTree set = new KdTree();

        AssertTrue(set.isEmpty());
        AssertTrue(set.size() == 0);
        Point2D p = new Point2D(0.5, 0.5);
        set.insert(p);
        AssertTrue(!set.isEmpty());
        AssertTrue(set.size() == 1);
        AssertTrue(set.contains(p));

        KdTreeNode root = set.get_root();
        AssertRect(new RectHV(0, 0, 1, 1), root.get_rect());
        AssertTrue(p == root.get_point());

        Point2D rightChild = new Point2D(0.75, 0.5);
        set.insert(rightChild);
        KdTreeNode rightNode = root.get_right();

        AssertTrue(set.size() == 2);
        AssertTrue(rightNode != null);
        AssertTrue(rightNode.get_point() == rightChild);
        AssertRect(new RectHV(0.5, 0, 1, 1), rightNode.get_rect());

        Point2D leftChild = new Point2D(0.25, 0.5);
        set.insert(leftChild);
        KdTreeNode leftNode = root.get_left();

        AssertTrue(set.size() == 3);
        AssertTrue(leftNode != null);
        AssertTrue(leftNode.get_point() == leftChild);
        AssertRect(new RectHV(0, 0, 0.5, 1), leftNode.get_rect());

        Point2D rightTopChild = new Point2D(0.75, 0.75);
        set.insert(rightTopChild);
        KdTreeNode rightTopNode = rightNode.get_right();

        AssertTrue(set.size() == 4);
        AssertTrue(rightTopNode != null);
        AssertTrue(rightTopNode.get_point() == rightTopChild);
        AssertRect(new RectHV(0.5, 0.5, 1, 1), rightTopNode.get_rect());

        Point2D rightBottomChild = new Point2D(0.75, 0.25);
        set.insert(rightBottomChild);
        KdTreeNode rightBottomNode = rightNode.get_left();

        AssertTrue(set.size() == 5);
        AssertTrue(rightBottomNode != null);
        AssertTrue(rightBottomNode.get_point() == rightBottomChild);
        AssertRect(new RectHV(0.5, 0, 1, 0.5), rightBottomNode.get_rect());

        Point2D leftTopChild = new Point2D(0.25, 0.75);
        set.insert(leftTopChild);
        KdTreeNode leftTopNode = leftNode.get_right();

        AssertTrue(set.size() == 6);
        AssertTrue(leftTopNode != null);
        AssertTrue(leftTopNode.get_point() == leftTopChild);
        AssertRect(new RectHV(0, 0.5, 0.5, 1), leftTopNode.get_rect());

        Point2D leftBottomChild = new Point2D(0.25, 0.25);
        set.insert(leftBottomChild);
        KdTreeNode leftBottomNode = leftNode.get_left();

        AssertTrue(set.size() == 7);
        AssertTrue(leftBottomNode != null);
        AssertTrue(leftBottomNode.get_point() == leftBottomChild);
        AssertRect(new RectHV(0, 0, 0.5, 0.5), leftBottomNode.get_rect());

        AssertTrue(set.nearest(new Point2D(0.5, 0.5)) == p);
        AssertTrue(set.nearest(new Point2D(0.25, 0.8)) == leftTopChild);
        AssertTrue(set.nearest(new Point2D(0, 0)) == leftBottomChild);

        ArrayList<Point2D> totalRange = (ArrayList<Point2D>) set.range(new RectHV(0, 0, 1, 1));
        AssertTrue(totalRange.size() == 7);

        ArrayList<Point2D> zeroRange = (ArrayList<Point2D>) set.range(new RectHV(0, 0, 0.1, 0.1));
        AssertTrue(zeroRange.isEmpty());
        */
/*
        KdTree set = new KdTree();
        set.insert(new Point2D(1, 1));
        set.insert(new Point2D(1.0, 0.5));
        set.insert(new Point2D(0.75, 1.0));
        set.insert(new Point2D(0.75, 0));

        set.insert(new Point2D(0, 1));
        set.insert(new Point2D(0.5, 0.5));
        set.insert(new Point2D(1, 0));
        set.insert(new Point2D(0, 0));
        set.insert(new Point2D(0, 0.5));
        set.insert(new Point2D(0.25, 0.75));

        set.draw();
        AssertTrue(set.contains(new Point2D(1.0, 0)));
        */
    }

    private KdTreeNode get_root() { return _root; }

    private static void AssertRect(RectHV expected, RectHV source) {
        AssertTrue(expected.xmin() == source.xmin());
        AssertTrue(expected.ymin() == source.ymin());
        AssertTrue(expected.xmax() == source.xmax());
        AssertTrue(expected.ymax() == source.ymax());
    }

    private static void AssertTrue(boolean flag) {
        if(!flag) {
        }
    }

    private void range(RectHV rect, KdTreeNode node, ArrayList<Point2D> points) {
        if(node == null) {
            return;
        }

        if(!node.get_rect().intersects(rect)) {
            return;
        }

        if(rect.contains(node.get_point())) {
            points.add(node.get_point());
        }

        range(rect, node.get_left(), points);
        range(rect, node.get_right(), points);
    }

    private Point2D nearest(KdTreeNode node, Point2D point, Point2D best) {
        if(node.get_point().distanceSquaredTo(point) < best.distanceSquaredTo(point)){
            best = node.get_point();
        }

        KdTreeNode left = node.get_left();
        if(left != null && left._rect.distanceSquaredTo(point) <= best.distanceSquaredTo(point)) {
            best = min(nearest(left, point, best), best, point);
        }

        KdTreeNode right = node.get_right();
        if(right != null && right._rect.distanceSquaredTo(point) <= best.distanceSquaredTo(point)) {
            best = min(nearest(right, point, best), best, point);
        }

        return best;
    }

    private Point2D min(Point2D left, Point2D right, Point2D destination) {
        return left.distanceSquaredTo(destination) < right.distanceSquaredTo(destination) ? left : right;
    }

    private boolean contains_vertical(KdTreeNode node, Point2D point) {
        Point2D node_point = node.get_point();
        if(point.x() == node_point.x() && point.y() == node_point.y()) {
            return true;
        }

        if(point.x() > node_point.x()) {
            return node.get_right() != null && contains_horizontal(node.get_right(), point);
        }
        else {
            return node.get_left() != null && contains_horizontal(node.get_left(), point);
        }
    }

    private boolean contains_horizontal(KdTreeNode node, Point2D point) {
        Point2D node_point = node.get_point();
        if(point.x() == node_point.x() && point.y() == node_point.y()) {
            return true;
        }

        if(point.y() > node_point.y()) {
            return node.get_right() != null && contains_vertical(node.get_right(), point);
        }
        else {
            return node.get_left() != null && contains_vertical(node.get_left(), point);
        }
    }

    private boolean insert_horizontal(KdTreeNode node, Point2D point, RectHV rect) {
        if(point.x() == node.get_point().x() && point.y() == node.get_point().y()) {
            return false;
        }

        if(point.y() > node.get_point().y()) {
            RectHV subRect = new RectHV(rect.xmin(), node.get_point().y(), rect.xmax(), rect.ymax());

            if(node.get_right() == null) {
                node.set_right(new KdTreeNode(point, subRect));
                return true;
            }
            else {
                return insert_vertical(node.get_right(), point, subRect);
            }
        }
        else {
            RectHV subRect = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), node.get_point().y());

            if(node.get_left() == null) {
                node.set_left(new KdTreeNode(point, subRect));
                return true;
            }
            else {
                return insert_vertical(node.get_left(), point, subRect);
            }
        }
    }

    private boolean insert_vertical(KdTreeNode node, Point2D point, RectHV rect) {
        if(point.x() == node.get_point().x() && point.y() == node.get_point().y()) {
            return false;
        }

        if(point.x() > node.get_point().x()) {
            RectHV subRect = new RectHV(node.get_point().x(), rect.ymin(), rect.xmax(), rect.ymax());

            if(node.get_right() == null) {
                node.set_right(new KdTreeNode(point, subRect));
                return true;
            }
            else {
                return insert_horizontal(node.get_right(), point, subRect);
            }
        }
        else {
            RectHV subRect = new RectHV(rect.xmin(), rect.ymin(), node.get_point().x(), rect.ymax());

            if(node.get_left() == null) {
                node.set_left(new KdTreeNode(point, subRect));
                return true;
            }
            else {
                return insert_horizontal(node.get_left(), point, subRect);
            }
        }
    }

    private int _nodesCount;
    private KdTreeNode _root;

    private final class KdTreeNode {
        public KdTreeNode(Point2D point, RectHV rect) {
            _left = null;
            _right = null;
            _point = point;
            _rect = rect;
        }

        public Point2D get_point() {
            return _point;
        }

        public KdTreeNode get_left() {
            return _left;
        }

        public void set_left(KdTreeNode left) {
            _left = left;
        }

        public KdTreeNode get_right() {
            return _right;
        }

        public void set_right(KdTreeNode right) {
            _right = right;
        }

        public RectHV get_rect() { return _rect; }

        public void draw(boolean isHorizontal) {
            StdDraw.setPenColor(isHorizontal ? StdDraw.BLUE : StdDraw.RED);
            _point.draw();
            if(_left != null) {
                _left.draw(!isHorizontal);
            }

            if(_right != null) {
                _right.draw(!isHorizontal);
            }
        }

        private KdTreeNode _left;
        private KdTreeNode _right;
        private final Point2D _point;
        private final RectHV _rect;
    }
}