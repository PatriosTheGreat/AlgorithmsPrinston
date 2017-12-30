import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class SAP {

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if(G == null) {
            throw new IllegalArgumentException();
        }

        digraph = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        checkVertex(v);
        checkVertex(w);

        return getMinAncestor(v, w).distence();
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        checkVertex(v);
        checkVertex(w);

        return getMinAncestor(v, w).id();
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if(v == null || w == null) {
            throw new IllegalArgumentException();
        }

        return getMinAncestor(v, w).distence();
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if(v == null || w == null) {
            throw new IllegalArgumentException();
        }

        return getMinAncestor(v, w).id();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    private Iterable<Integer> yield(Integer vertex) {
        return Arrays.asList(new Integer[] { vertex });
    }

    private MinAncestor getMinAncestor(Integer v, Integer w) {
        return getMinAncestor(yield(v), yield(w));
    }

    private MinAncestor getMinAncestor(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths pathsFromV = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths pathsFromW = new BreadthFirstDirectedPaths(digraph, w);
        int minDistance = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int i = 0; i < digraph.V(); i++) {
            if(pathsFromV.hasPathTo(i) && pathsFromW.hasPathTo(i)) {
                Integer distance = pathsFromV.distTo(i) + pathsFromW.distTo(i);
                if(distance < minDistance) {
                    ancestor = i;
                    minDistance = distance;
                }
            }
        }

        if(ancestor == -1) {
            return new MinAncestor(-1, -1);
        }

        return new MinAncestor(pathsFromV.distTo(ancestor) + pathsFromW.distTo(ancestor), ancestor);
    }

    private void checkVertex(int id) {
        if(id < 0 || id > digraph.V()) {
            throw new IllegalArgumentException();
        }
    }

    private Digraph digraph;

    private class MinAncestor {
        public MinAncestor(int distence, int id){
            _id = id;
            _distence = distence;
        }

        public int distence(){
            return _distence;
        }

        public int id() {
            return _id;
        }

        private int _distence;
        private int _id;
    }
}
