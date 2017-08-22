import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    public PercolationStats(int n, int trials) {
        double[] percolationThresholds = new double[trials];
        for(int i = 0; i < trials; i++){
            percolationThresholds[i] = PercolateAndGetTreshold(n);
        }

        _mean = StdStats.mean(percolationThresholds);
        _stddev = StdStats.stddev(percolationThresholds);
        double confidenceFraction = (1.96 * stddev()) / Math.sqrt(trials);
        _confidenceLo = _mean - confidenceFraction;
        _confidenceHi = _mean + confidenceFraction;
    }

    public double mean() {
        return _mean;
    }

    public double stddev() {
        return _stddev;
    }

    public double confidenceLo() {
        return _confidenceLo;
    }

    public double confidenceHi() {
        return _confidenceHi;
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trails = Integer.parseInt(args[1]);
        if(n < 1 || trails < 1){
            throw new IllegalArgumentException();
        }

        PercolationStats percolationStats = new PercolationStats(n, trails);
        System.out.println("mean                    = " + percolationStats.mean());
        System.out.println("stddev                  = " + percolationStats.stddev());
        System.out.println("95% confidence interval = " + percolationStats.confidenceLo() + ", " + percolationStats.confidenceHi());
    }

    private double PercolateAndGetTreshold(int n){
        Percolation percolation = new Percolation(n);
        while(!percolation.percolates()){
            int column = 1 + StdRandom.uniform(n);
            int row = 1 + StdRandom.uniform(n);
            if(!percolation.isOpen(row, column)) {
                percolation.open(row, column);
            }
        }

        return percolation.numberOfOpenSites() / (double)(n * n);
    }

    private double _mean;
    private double _stddev;
    private double _confidenceLo;
    private double _confidenceHi;
}