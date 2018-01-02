import java.awt.Color;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    public SeamCarver(Picture picture) {             // create a seam carver object based on the given picture
        if(picture == null) {
            throw new IllegalArgumentException();
        }

        _width = picture.width();
        _height = picture.height();

        _energies = new double[width()][height()];
        _colors = new Color[width()][height()];

        for(int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                _colors[col][row] = picture.get(col, row);
            }
        }

        initializeEnergies();
    }

    public Picture picture() {                       // current picture
        Picture picture = new Picture(width(), height());

        for (int col = 0; col < width(); col++) {
            for(int row = 0; row < height(); row++) {
                picture.set(col, row, _colors[col][row]);
            }
        }

        return picture;
    }

    public int width() {                             // width of current picture
        return _width;
    }

    public int height() {                            // height of current picture
        return _height;
    }

    public double energy(int x, int y) {             // energy of pixel at column x and row y
        if(!inRange(x, width()) || !inRange(y, height())) {
            throw new IllegalArgumentException();
        }

        return _energies[x][y];
    }

    public int[] findHorizontalSeam() {              // sequence of indices for horizontal seam
        return new AcyclicSPHelper(height(), width(), _energies, true).getSP();
    }

    public int[] findVerticalSeam() {                // sequence of indices for vertical seam
        return new AcyclicSPHelper(height(), width(), _energies, false).getSP();
    }

    public void removeHorizontalSeam(int[] seam) {   // remove horizontal seam from current picture
        if(height() <= 1 || !isSeamValid(seam, width(), height())) {
            throw new IllegalArgumentException();
        }

        for(int col = 0; col < width(); col++) {
            int removedRow = seam[col];
            for(int row = removedRow; row < height() - 1; row++) {
                _colors[col][row] = _colors[col][row + 1];
                _energies[col][row] = _energies[col][row + 1];
            }
        }

        _height--;

        for(int col = 0; col < width(); col++) {
            int row = seam[col];
            initializeEnergy(col - 1, row);
            initializeEnergy(col, row - 1);
            initializeEnergy(col, row);
            initializeEnergy(col + 1, row);
            initializeEnergy(col, row + 1);
        }
    }

    public void removeVerticalSeam(int[] seam) {     // remove vertical seam from current picture
        if(width() <= 1 || !isSeamValid(seam, height(), width())) {
            throw new IllegalArgumentException();
        }

        for(int row = 0; row < height(); row++) {
            int removedCol = seam[row];
            for(int col = removedCol; col < width() - 1; col++) {
                _colors[col][row] = _colors[col + 1][row];
                _energies[col][row] = _energies[col + 1][row];
            }
        }

        _width--;

        for(int row = 0; row < height(); row++) {
            int col = seam[row];
            initializeEnergy(col - 1, row);
            initializeEnergy(col, row - 1);
            initializeEnergy(col, row);
            initializeEnergy(col + 1, row);
            initializeEnergy(col, row + 1);
        }
    }

    private static void AssertTrue(boolean condition) {
        if(condition == false) {
            throw new IllegalArgumentException();
        }
    }

    private static Picture CreatePicture(int width, int height, Color[][] colors) {
        Picture picture = new Picture(width, height);

        for (int col = 0; col < width; col++) {
            for(int row = 0; row < height; row++) {
                picture.set(col, row, colors[col][row]);
            }
        }

        return picture;
    }

    private void initializeEnergies() {
        for(int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                initializeEnergy(col, row);
            }
        }
    }

    private void initializeEnergy(int col, int row) {
        if(col < 0 || col >= width() || row < 0 || row >= height()) {
            return;
        }

        int lastCol = width() - 1;
        int lastRow = height() - 1;

        if(row == 0 || row == lastRow || col == 0 || col == lastCol) {
            _energies[col][row] = MaxEnergy;
            return;
        }

        Color top = _colors[col][row - 1];
        Color bottom = _colors[col][row + 1];
        Color left = _colors[col - 1][row];
        Color right = _colors[col + 1][row];

        int rowDeltaSquare = calculateDeltaSquare(bottom, top);
        int colDeltaSquare = calculateDeltaSquare(left, right);

        _energies[col][row] = Math.sqrt(rowDeltaSquare + colDeltaSquare);
    }

    private int calculateDeltaSquare(Color low, Color up) {
        int deltaRed = up.getRed() - low.getRed();
        int deltaGreen = up.getGreen() - low.getGreen();
        int deltaBlue = up.getBlue() - low.getBlue();

        return deltaRed * deltaRed + deltaGreen * deltaGreen + deltaBlue * deltaBlue;
    }

    private boolean isSeamValid(int[] seam, int length, int boundary) {
        if(seam == null || seam.length != length) {
            return false;
        }

        for(int i = 0; i < seam.length; i++) {
            if(!inRange(seam[i], boundary)) {
                return false;
            }

            if(i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1) {
                return false;
            }
        }

        return true;
    }

    private boolean inRange(int x, int boundary) {
        return x >= 0 && x < boundary;
    }

    private int _width;
    private int _height;
    private final double[][] _energies;
    private final Color[][] _colors;

    private static final double MaxEnergy = 1000;

    private class AcyclicSPHelper {
        public AcyclicSPHelper(int height, int width, double[][] weights, boolean isHorizontal) {
            _weights = weights;
            _height = height;
            _width = width;

            _distances = new double[width][height];
            for(int col = 0; col < width(); col++) {
                for (int row = 0; row < height(); row++) {
                    _distances[col][row] = Double.POSITIVE_INFINITY;
                }
            }

            _edgesFrom = new int[width][height];

            if(isHorizontal) {
                initializeHorizontalSP();
            }
            else {
                initializeVerticalSP();
            }
        }

        public int[] getSP() {
            return _sp;
        }

        private void initializeHorizontalSP() {
            int lastCol = width() - 1;
            for(int row = 0; row < height(); row++) {
                _distances[0][row] = MaxEnergy;
            }

            for(int col = 1; col < width(); col++) {
                for (int row = 1; row < height(); row++) {
                    int prevCellTopRow = row - 1;
                    int prevCellBottomRow = row + 1;
                    int prevCellMiddleRow = row;
                    int prevCellCol = col - 1;

                    relax(prevCellCol, prevCellTopRow, col, row, prevCellTopRow);
                    relax(prevCellCol, prevCellBottomRow, col, row, prevCellBottomRow);
                    relax(prevCellCol, prevCellMiddleRow, col, row, prevCellMiddleRow);
                }
            }

            double minDistance = Double.POSITIVE_INFINITY;
            int minDistanceRow = 0;
            for(int row = 0; row < height(); row++) {
                if(_distances[lastCol][row] < minDistance) {
                    minDistance = _distances[lastCol][row];
                    minDistanceRow = row;
                }
            }

            _sp = new int[_width];
            int currentMinRow = minDistanceRow;
            for(int col = lastCol; col >= 0; col--) {
                _sp[col] = currentMinRow;
                if(col > 0) {
                    currentMinRow = _edgesFrom[col][currentMinRow];
                }
            }
        }

        private void relax(int fromCol, int fromRow, int toCol, int toRow, int index) {
            if(fromCol < 0 || fromRow < 0 || fromCol >= width() || fromRow >= height()) {
                return;
            }

            double currentDistance = _distances[fromCol][fromRow] + _weights[toCol][toRow];
            if(currentDistance < _distances[toCol][toRow]) {
                _distances[toCol][toRow] = currentDistance;
                _edgesFrom[toCol][toRow] = index;
            }
        }

        private void initializeVerticalSP() {
            int lastRow = height() - 1;

            for(int col = 0; col < width(); col++) {
                _distances[col][0] = MaxEnergy;
            }

            for (int row = 1; row < height(); row++) {
                for(int col = 1; col < width(); col++) {
                    int prevCellRightCol = col + 1;
                    int prevCellLeftCol = col - 1;
                    int prevCellMiddleCol = col;
                    int prevCellRow = row - 1;

                    relax(prevCellRightCol, prevCellRow, col, row, prevCellRightCol);
                    relax(prevCellLeftCol, prevCellRow, col, row, prevCellLeftCol);
                    relax(prevCellMiddleCol, prevCellRow, col, row, prevCellMiddleCol);
                }
            }

            double minDistance = Double.POSITIVE_INFINITY;
            int minDistanceCol = 0;
            for(int col = 0; col < width(); col++) {
                if(_distances[col][lastRow] < minDistance) {
                    minDistance = _distances[col][lastRow];
                    minDistanceCol = col;
                }
            }

            _sp = new int[_height];
            int currentMinCol = minDistanceCol;
            for(int row = lastRow; row >= 0; row--) {
                _sp[row] = currentMinCol;
                if(row > 0) {
                    currentMinCol = _edgesFrom[currentMinCol][row];
                }
            }
        }

        private int[] _sp;

        private final int _width;
        private final int _height;
        private final double[][] _weights;
        private final double[][] _distances;
        private final int[][] _edgesFrom;
    }



    public static void main(String[] args) {
        Picture onePixelPicture =
                CreatePicture(1, 1, new Color[][] { new Color[] { new Color(0, 0, 0) } });

        SeamCarver seamCarver = new SeamCarver(onePixelPicture);

        AssertTrue(seamCarver.width() == 1);
        AssertTrue(seamCarver.height() == 1);
        AssertTrue(seamCarver.energy(0, 0) == MaxEnergy);
        AssertTrue(seamCarver.findHorizontalSeam().length == 1);
        AssertTrue(seamCarver.findHorizontalSeam()[0] == 0);
        AssertTrue(seamCarver.findVerticalSeam().length == 1);
        AssertTrue(seamCarver.findVerticalSeam()[0] == 0);

        Picture one2TwoPixelsPicture =
                CreatePicture(
                        1,
                        2,
                        new Color[][] {
                                new Color[] {
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0)
                                }
                        });

        seamCarver = new SeamCarver(one2TwoPixelsPicture);

        AssertTrue(seamCarver.width() == 1);
        AssertTrue(seamCarver.height() == 2);
        AssertTrue(seamCarver.energy(0, 0) == MaxEnergy);
        AssertTrue(seamCarver.energy(0, 1) == MaxEnergy);
        AssertTrue(seamCarver.findHorizontalSeam().length == 1);
        AssertTrue(seamCarver.findHorizontalSeam()[0] == 0);
        AssertTrue(seamCarver.findVerticalSeam().length == 2);
        AssertTrue(seamCarver.findVerticalSeam()[0] == 0);
        AssertTrue(seamCarver.findVerticalSeam()[1] == 0);


        Picture two2OnePixelsPicture =
                CreatePicture(
                        2,
                        1,
                        new Color[][] {
                                new Color[] {
                                        new Color(0, 0, 0)
                                },
                                new Color[] {
                                        new Color(0, 0, 0)
                                }
                        });

        seamCarver = new SeamCarver(two2OnePixelsPicture);

        AssertTrue(seamCarver.width() == 2);
        AssertTrue(seamCarver.height() == 1);
        AssertTrue(seamCarver.energy(0, 0) == MaxEnergy);
        AssertTrue(seamCarver.energy(1, 0) == MaxEnergy);
        AssertTrue(seamCarver.findHorizontalSeam().length == 2);
        AssertTrue(seamCarver.findHorizontalSeam()[0] == 0);
        AssertTrue(seamCarver.findHorizontalSeam()[1] == 0);
        AssertTrue(seamCarver.findVerticalSeam().length == 1);
        AssertTrue(seamCarver.findVerticalSeam()[0] == 0);

        Picture two2TwoPixelsPicture =
                CreatePicture(
                        2,
                        2,
                        new Color[][] {
                                new Color[] {
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0)
                                },
                                new Color[] {
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0)
                                }
                        });

        seamCarver = new SeamCarver(two2TwoPixelsPicture);

        AssertTrue(seamCarver.width() == 2);
        AssertTrue(seamCarver.height() == 2);
        AssertTrue(seamCarver.energy(0, 0) == MaxEnergy);
        AssertTrue(seamCarver.energy(1, 0) == MaxEnergy);
        AssertTrue(seamCarver.energy(0, 1) == MaxEnergy);
        AssertTrue(seamCarver.energy(1, 1) == MaxEnergy);
        AssertTrue(seamCarver.findHorizontalSeam().length == 2);
        AssertTrue(seamCarver.findHorizontalSeam()[0] == 0);
        AssertTrue(seamCarver.findHorizontalSeam()[1] == 1);
        AssertTrue(seamCarver.findVerticalSeam().length == 2);
        AssertTrue(seamCarver.findVerticalSeam()[0] == 0);
        AssertTrue(seamCarver.findVerticalSeam()[1] == 1);

        Picture tree2TreePixelsPicture =
                CreatePicture(
                        3,
                        3,
                        new Color[][] {
                                new Color[] {
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0)
                                },

                                new Color[] {
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0)
                                },
                                new Color[] {
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0)
                                }
                        });

        seamCarver = new SeamCarver(tree2TreePixelsPicture);

        AssertTrue(seamCarver.width() == 3);
        AssertTrue(seamCarver.height() == 3);
        AssertTrue(seamCarver.energy(1, 1) == 0);
        int[] horizontalSeam = seamCarver.findHorizontalSeam();
        AssertTrue(horizontalSeam.length == 3);
        AssertTrue(horizontalSeam[0] == 0);
        AssertTrue(horizontalSeam[1] == 1);
        AssertTrue(horizontalSeam[2] == 1);

        int[] verticalSeam = seamCarver.findHorizontalSeam();
        AssertTrue(verticalSeam.length == 3);
        AssertTrue(verticalSeam[0] == 0);
        AssertTrue(verticalSeam[1] == 1);
        AssertTrue(verticalSeam[2] == 1);

        seamCarver.removeHorizontalSeam(horizontalSeam);
        AssertTrue(seamCarver.width() == 3);
        AssertTrue(seamCarver.height() == 2);
        AssertTrue(seamCarver.energy(1, 1) == MaxEnergy);

        seamCarver = new SeamCarver(tree2TreePixelsPicture);
        seamCarver.removeVerticalSeam(horizontalSeam);
        AssertTrue(seamCarver.width() == 2);
        AssertTrue(seamCarver.height() == 3);
        AssertTrue(seamCarver.energy(1, 1) == MaxEnergy);

        Picture four2SixPixelsPicture =
                CreatePicture(
                        4,
                        6,
                        new Color[][] {
                                new Color[] {
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0)
                                },

                                new Color[] {
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0)
                                },
                                new Color[] {
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0)
                                },
                                new Color[] {
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0)
                                }
                        });

        seamCarver = new SeamCarver(four2SixPixelsPicture);

        AssertTrue(seamCarver.width() == 4);
        AssertTrue(seamCarver.height() == 6);
        verticalSeam = seamCarver.findVerticalSeam();
        AssertTrue(verticalSeam.length == 6);
        AssertTrue(verticalSeam[0] == 2);
        AssertTrue(verticalSeam[1] == 1);
        AssertTrue(verticalSeam[2] == 2);

        Picture four2FourPixelsPicture =
                CreatePicture(
                        4,
                        4,
                        new Color[][] {
                                new Color[] {
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0)
                                },

                                new Color[] {
                                        new Color(0, 0, 0),
                                        new Color(20, 0, 0),
                                        new Color(10, 0, 0),
                                        new Color(0, 0, 0)
                                },
                                new Color[] {
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(20, 0, 0),
                                        new Color(0, 0, 0)
                                },
                                new Color[] {
                                        new Color(0, 0, 0),
                                        new Color(0, 0, 0),
                                        new Color(0, 5, 0),
                                        new Color(0, 0, 0)
                                }
                        });

        seamCarver = new SeamCarver(four2FourPixelsPicture);
        AssertTrue(seamCarver.width() == 4);
        AssertTrue(seamCarver.height() == 4);
        AssertTrue(seamCarver.energy(0, 0) == MaxEnergy);
        AssertTrue(seamCarver.energy(0, 1) == MaxEnergy);
        AssertTrue(seamCarver.energy(0, 2) == MaxEnergy);
        AssertTrue(seamCarver.energy(0, 3) == MaxEnergy);

        AssertTrue(seamCarver.energy(1, 0) == MaxEnergy);
        AssertTrue(seamCarver.energy(1, 1) == 10.0);
        AssertTrue(seamCarver.energy(1, 2) - 28.28 < 0.1);
        AssertTrue(seamCarver.energy(1, 3) == MaxEnergy);

        AssertTrue(seamCarver.energy(2, 0) == MaxEnergy);
        AssertTrue(seamCarver.energy(2, 1) - 28.28 < 0.1);
        AssertTrue(seamCarver.energy(2, 2) - 11.18 < 0.1);
        AssertTrue(seamCarver.energy(2, 3) == MaxEnergy);

        AssertTrue(seamCarver.energy(3, 0) == MaxEnergy);
        AssertTrue(seamCarver.energy(3, 1) == MaxEnergy);
        AssertTrue(seamCarver.energy(3, 2) == MaxEnergy);
        AssertTrue(seamCarver.energy(3, 3) == MaxEnergy);

        verticalSeam = seamCarver.findVerticalSeam();
        AssertTrue(verticalSeam.length == 4);
        AssertTrue(verticalSeam[1] == 1);
        AssertTrue(verticalSeam[2] == 2);

        horizontalSeam = seamCarver.findHorizontalSeam();
        AssertTrue(horizontalSeam.length == 4);
        AssertTrue(horizontalSeam[1] == 1);
        AssertTrue(horizontalSeam[2] == 2);

        Picture four2OnePixelsPicture =
                CreatePicture(
                        4,
                        1,
                        new Color[][] {
                                new Color[] {
                                        new Color(0, 0, 0)
                                },

                                new Color[] {
                                        new Color(0, 0, 0)
                                },
                                new Color[] {
                                        new Color(0, 0, 0)
                                },
                                new Color[] {
                                        new Color(0, 0, 0)
                                }
                        });

        seamCarver = new SeamCarver(four2OnePixelsPicture);
        AssertTrue(seamCarver.width() == 4);
        AssertTrue(seamCarver.height() == 1);

        seamCarver.removeVerticalSeam(new int[] { 2 });

        AssertTrue(seamCarver.width() == 3);
        AssertTrue(seamCarver.height() == 1);
    }
}
