import edu.princeton.cs.algs4.StdDraw;

import java.util.Comparator;

public class Point implements Comparable<Point> {
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw() {
        StdDraw.point(x, y);
    }

    public void drawTo(Point that) {
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public int compareTo(Point that) {
        return this.y == that.y
                ? this.x > that.x
                    ? 1
                    : this.x < that.x
                        ? -1
                        : 0
                : this.y > that.y ? 1 : -1;
    }

    public double slopeTo(Point that) {
        if(this.x == that.x && this.y == that.y) {
            return Double.NEGATIVE_INFINITY;
        }

        if(this.x == that.x) {
            return Double.POSITIVE_INFINITY;
        }

        if(this.y == that.y) {
            return 0.0;
        }

        return (that.y - this.y) / (double)(that.x - this.x);
    }

    public Comparator<Point> slopeOrder() {
        return (first, second) ->
        {
            double firstSlope = this.slopeTo(first);
            double secondSlope = this.slopeTo(second);
            return firstSlope == secondSlope ? 0 : firstSlope < secondSlope ? -1 : 1;
        };
    }

    public static void main(String[] args) {
    }

    private int x;
    private int y;
}
