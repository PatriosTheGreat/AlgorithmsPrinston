import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    public Percolation(int n)                // create n-by-n grid, with all sites blocked
    {
        _n = n;
        _size = n * n + 2;
        _finder = new WeightedQuickUnionUF(_size);
        _isOpened = new boolean[_size];

        for(int i = 1; i <= _n; i++){
            _finder.union(0, getIndex(1, i));
            _finder.union(_size - 1, getIndex(_n, i));
        }
    }

    public void open(int row, int col)    // open site (row, col) if it is not open already
    {
        checkRowColumn(row);
        checkRowColumn(col);
        int index = getIndex(row, col);

        int leftIndex = getIndexChecked(row, col - 1);
        if(leftIndex != -1 && _isOpened[leftIndex]){
            _finder.union(leftIndex, index);
        }

        int rightIndex = getIndexChecked(row, col + 1);
        if(rightIndex != -1 && _isOpened[rightIndex]){
            _finder.union(rightIndex, index);
        }

        int topIndex = getIndexChecked(row - 1, col);
        if(topIndex != -1 && _isOpened[topIndex]){
            _finder.union(topIndex, index);
        }

        int bottomIndex = getIndexChecked(row + 1, col);
        if(bottomIndex != -1 && _isOpened[bottomIndex]){
            _finder.union(bottomIndex, index);
        }

        _numberOfOpened++;
        _isOpened[index] = true;
    }

    public boolean isOpen(int row, int col)  // is site (row, col) open?
    {
        checkRowColumn(row);
        checkRowColumn(col);

        return _isOpened[getIndex(row, col)];
    }

    public boolean isFull(int row, int col)  // is site (row, col) full?
    {
        checkRowColumn(row);
        checkRowColumn(col);

        return _finder.connected(0, getIndex(row, col)) && isOpen(row, col);
    }

    public int numberOfOpenSites()       // number of open sites
    {
        return _numberOfOpened;
    }

    public boolean percolates()              // does the system percolate?
    {
        return _finder.connected(0, _size-1);
    }

    public static void main(String[] args)
    {
        Percolation percolation = new Percolation(1);
        // percolation.open(1, 6);
        System.out.println(percolation.percolates());
        // System.out.println(percolation.isOpen(1, 6));
        return;
    }

    private boolean isOpenIndex(int index){
        return index > 0 && index < _size - 1 && _isOpened[index];
    }

    private int getIndex(int row, int column){
        return (row - 1) * _n + column;
    }

    private int getIndexChecked(int row, int column){
        return
                row < 1 || row > _n || column < 1 || column > _n
                        ? -1
                        : (row - 1) * _n + column;
    }

    private void checkRowColumn(int rowColumn){
        if(rowColumn < 1 || rowColumn > _n){
            throw new IllegalArgumentException();
        }
    }

    private int _numberOfOpened = 0;
    private int _size;
    private int _n;
    private boolean[] _isOpened;
    private WeightedQuickUnionUF _finder;
/*
    private class WeightedQuickUnionFinder {
        public WeightedQuickUnionFinder(int size){
            _roots = new int[size];
            _weights = new int[size];

            for(int i = 0; i < _weights.length; i++){
                _weights[i] = 1;
                _roots[i] = i;
            }
        }

        public boolean isConnected(int q, int p){
            return findRoot(q) == findRoot(p);
        }

        public void union(int q, int p){
            int qRoot = findRoot(q);
            int pRoot = findRoot(p);

            if(_weights[pRoot] < _weights[qRoot]){
                _roots[pRoot] = qRoot;
                _weights[qRoot] += _weights[pRoot];
            }
            else {
                _roots[qRoot] = pRoot;
                _weights[pRoot] = _weights[qRoot];
            }
        }

        private int findRoot(int node){
            while(_roots[node] != node){
                _roots[node] = _roots[_roots[node]];
                node = _roots[node];
            }

            return node;
        }

        private int[] _roots;
        private int[] _weights;
    }*/
}
