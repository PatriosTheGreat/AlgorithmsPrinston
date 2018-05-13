import java.util.Arrays;

public class CircularSuffixArray {
    public CircularSuffixArray(String s) {
        if(s == null) {
            throw new IllegalArgumentException();
        }

        indexes = new Integer[s.length()];
        calculateIndexes(s);
    }

    public int length() {
        return indexes.length;
    }

    public int index(int i) {
        if(i < 0 || i >= indexes.length) {
            throw new IllegalArgumentException();
        }

        return indexes[i];
    }

    public static void main(String[] args) {
        CircularSuffixArray array = new CircularSuffixArray("ABRACADABRA!");
        System.out.println(array.length());
        System.out.println();
        for(int i = 0; i < 12; i++) {
            System.out.println(array.index(i));
        }
    }

    private void calculateIndexes(String s) {
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }

        Arrays.sort(
            indexes,
            (left, right) -> {
                for (int i = 0; i < s.length(); i++) {
                    char leftChar = s.charAt((left + i) % s.length());
                    char rightChar = s.charAt((right + i) % s.length());

                    if(leftChar != rightChar) {
                        return (int) Math.signum(leftChar - rightChar);
                    }
                }

                return (int) Math.signum(right - left);
            });
    }

    private final Integer[] indexes;
}