import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class Solver {
    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if(initial == null) {
            throw new IllegalArgumentException();
        }

        SearchNode rootNode = new SearchNode(initial, null, 0);
        MinPQ<SearchNode> rootSearch = new MinPQ<>();
        rootSearch.insert(rootNode);

        SearchNode twinNode = new SearchNode(initial.twin(), null, 0);
        MinPQ<SearchNode> twinSearch = new MinPQ<>();
        twinSearch.insert(twinNode);

        do {
            _goalNode = makeSearchStepAndGetGoalOrNull(rootSearch);
            twinNode = makeSearchStepAndGetGoalOrNull(twinSearch);
        } while(_goalNode == null && twinNode == null);
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return _goalNode != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return isSolvable() ? _goalNode.moves() : -1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if(!isSolvable()) {
            return null;
        }

        ArrayList<SearchNode> nodes = new ArrayList<>();
        SearchNode currentNode = _goalNode;
        while(currentNode != null) {
            nodes.add(currentNode);
            currentNode = currentNode.previousNode();
        }

        return reverseAndGetBoards(nodes);
    }

    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                blocks[i][j] = in.readInt();
            }
        }

        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

    private ArrayList<Board> reverseAndGetBoards(ArrayList<SearchNode> nodes) {
        int size = nodes.size();
        ArrayList<Board> result = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            result.add(nodes.get(size - i - 1).board());
        }

        return result;
    }

    private SearchNode makeSearchStepAndGetGoalOrNull(MinPQ<SearchNode> searchQueue) {
        if(searchQueue.isEmpty()) {
            return null;
        }

        SearchNode min = searchQueue.delMin();
        if(min.board().isGoal()) {
            return min;
        }

        for (Board neighbor : min.board().neighbors()) {
            if(min.previousNode() == null || !min.previousNode().board().equals(neighbor)) {
                searchQueue.insert(new SearchNode(neighbor, min, min.moves() + 1));
            }
        }

        return null;
    }

    private SearchNode _goalNode = null;

    private final class SearchNode implements Comparable<SearchNode> {
        public SearchNode(Board board, SearchNode previousNode, int move) {
            _board = board;
            _move = move;
            _previousNode = previousNode;
        }

        public int moves() {
            return _move;
        }

        public int priority() {
            return moves() + _board.manhattan();
        }

        public Board board() {
            return _board;
        }

        public SearchNode previousNode() {
            return _previousNode;
        }

        @Override
        public int compareTo(SearchNode other) {
            if(other == null) {
                return -1;
            }

            return sign((board().manhattan() - other.board().manhattan()) + (moves() - other.moves()));
        }

        private int sign(int x) {
            return x > 0 ? 1 : x < 0 ? -1 : 0;
        }

        private final int _move;
        private final Board _board;
        private final SearchNode _previousNode;
    }
}