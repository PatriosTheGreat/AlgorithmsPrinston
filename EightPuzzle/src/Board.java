import java.util.ArrayList;

public class Board {
    public Board(int[][] blocks) {
        _blocks = blocks;
    }

    public int dimension() {
        return _blocks.length;
    }

    // number of blocks out of place
    public int hamming() {
        initializeHamming();
        return _hamming;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        initializeManhattan();
        return _manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin() {
        Board twin = copy();

        for(int i = 0; i < dimension(); i++)
        {
            for(int j = 0; j < dimension(); j++)
            {
                if(getBlockSafe(i, j) > 0 && getBlockSafe(i + 1, j) > 0)
                {
                    twin.swap(i, j, i + 1, j);
                    return twin;
                }

                if(getBlockSafe(i, j) > 0 && getBlockSafe(i, j + 1) > 0)
                {
                    twin.swap(i, j, i, j + 1);
                    return twin;
                }
            }
        }

        if(twin.equals(this))
        {
            return null;
        }

        return twin;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if(y == null)
        {
            return false;
        }

        if(y == this)
        {
            return true;
        }

        if(y.getClass() != this.getClass())
        {
            return false;
        }

        Board other = (Board) y;
        if(other.dimension() != dimension())
        {
            return false;
        }

        for(int i = 0; i < _blocks.length; i++)
        {
            for(int j = 0; j < _blocks.length; j++)
            {
                if(_blocks[i][j] != other._blocks[i][j])
                {
                    return false;
                }
            }
        }

        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        initializeEmpty();
        ArrayList<Board> neighbors = new ArrayList<>();
        int leftNeighborI = _emptyI;
        int leftNeighborJ = _emptyJ - 1;
        addNeighborIfExists(leftNeighborI, leftNeighborJ, neighbors);

        int rightNeighborI = _emptyI;
        int rightNeighborJ = _emptyJ + 1;
        addNeighborIfExists(rightNeighborI, rightNeighborJ, neighbors);

        int topNeighborI = _emptyI - 1;
        int topNeighborJ = _emptyJ;
        addNeighborIfExists(topNeighborI, topNeighborJ, neighbors);

        int bottomNeighborI = _emptyI + 1;
        int bottomNeighborJ = _emptyJ;
        addNeighborIfExists(bottomNeighborI, bottomNeighborJ, neighbors);

        return neighbors;
    }

    // string representation of this board (in the output format specified below)
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(String.format("%2d%n", dimension()));
        for (int i = 0; i < _blocks.length; i++)
        {
            for (int j = 0; j < _blocks.length; j++)
            {
                result.append(String.format("%2d ", _blocks[i][j]));
            }

            result.append(String.format("%n"));
        }

        return result.toString();
    }

    // unit tests (not graded)
    public static void main(String[] args) {

    }

    private void addNeighborIfExists(int neighborI, int neighborJ, ArrayList<Board> neighbors) {
        if(neighborI >= 0 && neighborI < dimension() && neighborJ >= 0 && neighborJ < dimension())
        {
            Board neighbor = copy();
            neighbor.swap(_emptyI, _emptyJ, neighborI, neighborJ);
            neighbors.add(neighbor);
        }
    }

    private int getBlockSafe(int i, int j) {
        if(i > dimension() || i < 0 || j > dimension() || j < 0)
        {
            return -1;
        }

        return _blocks[i][j];
    }

    private Board copy() {
        int[][] blocks = new int[dimension()][dimension()];
        for(int i = 0; i < dimension(); i++)
        {
            for(int j = 0; j < dimension(); j++)
            {
                blocks[i][j] = _blocks[i][j];
            }
        }

        return new Board(blocks);
    }

    private void swap(int sourceI, int sourceJ, int targetI, int targetJ) {
        int target = _blocks[targetI][targetJ];
        _blocks[targetI][targetJ] = _blocks[sourceI][sourceJ];
        _blocks[sourceI][sourceJ] = target;
    }

    private void initializeHamming() {
        if(_hamming != -1)
        {
            return;
        }

        _hamming = 0;
        for(int i = 0; i < _blocks.length; i++)
        {
            for(int j = 0; j < _blocks[i].length; j++)
            {
                if(_blocks[i][j] != (i * _blocks.length + j + 1))
                {
                    _hamming++;
                }
            }
        }

        // reduce empty cell position
        _hamming--;
    }

    private void initializeManhattan() {
        if(_manhattan != -1)
        {
            return;
        }

        _manhattan = 0;
        for(int i = 0; i < _blocks.length; i++)
        {
            for(int j = 0; j < _blocks.length; j++)
            {
                _manhattan += distanceToGoal(i, j);
            }
        }
    }

    private void initializeEmpty() {
        for(int i = 0; i < dimension(); i++)
        {
            for (int j = 0; j < dimension(); j++)
            {
                if(_blocks[i][j] == 0)
                {
                    _emptyI = i;
                    _emptyJ = j;
                    return;
                }
            }
        }
    }

    private int distanceToGoal(int i, int j) {
        int value = _blocks[i][j];
        if(value == 0)
        {
            return 0;
        }

        value--;
        int goalI = value / dimension();
        int goalJ = value % dimension();
        return Math.abs(goalI - i) + Math.abs(goalJ - j);
    }

    private int _emptyI = -1;
    private int _emptyJ = -1;
    private int _manhattan = -1;
    private int _hamming = -1;
    private final int[][] _blocks;
}