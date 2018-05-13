import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Arrays;
import java.util.LinkedList;

public class BurrowsWheeler {
    public static void transform() {
        String input = BinaryStdIn.readString();

        CircularSuffixArray array = new CircularSuffixArray(input);
        for(int i = 0; i < array.length(); i++) {
            if(array.index(i) == 0) {
                BinaryStdOut.write(i);
            }
        }

        for(int i = 0; i < array.length(); i++) {
            BinaryStdOut.write(input.charAt((array.index(i) + array.length() - 1) % array.length()));
        }

        BinaryStdOut.close();
    }

    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String input = BinaryStdIn.readString();
        int[] next = constructNext(input);

        int currentIndex = next[first];
        for(int i = 0; i < input.length(); i++) {
            BinaryStdOut.write(input.charAt(currentIndex));
            currentIndex = next[currentIndex];
        }

        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected 1 argument");
        }

        switch (args[0]){
            case "-":
                transform();
                break;
            case "+":
                inverseTransform();
                break;
            default:
                throw new IllegalArgumentException("Argument could be only + or -");
        }
    }

    private static int[] constructNext(String input) {
        int[] next = new int[input.length()];

        LinkedList<Integer>[] indexesList = new LinkedList[256];
        for(int i = 0; i < input.length(); i++) {
            LinkedList<Integer> list = indexesList[input.charAt(i)];
            if(list == null) {
                list = new LinkedList<>();
            }

            list.add(i);

            indexesList[input.charAt(i)] = list;
        }

        String sorted = sort(input);
        for(int i = 0; i < input.length(); i++) {
            next[i] = indexesList[sorted.charAt(i)].pop();
        }

        return next;
    }

    private static String sort(String str) {
        char[] chars = str.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}