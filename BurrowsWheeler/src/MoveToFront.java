import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] alphabet = getAlphabet();

        while (!BinaryStdIn.isEmpty()) {
            char symbol = BinaryStdIn.readChar();
            int index = swapToFront(alphabet, symbol);
            BinaryStdOut.write(index, 8);
            BinaryStdOut.flush();
        }

        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] alphabet = getAlphabet();

        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readChar();
            char symbol = alphabet[index];
            swapToFront(alphabet, symbol);
            BinaryStdOut.write(symbol, 8);
        }

        BinaryStdOut.close();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected 1 argument");
        }

        switch (args[0]){
            case "-":
                encode();
                break;
            case "+":
                decode();
                break;
            default:
                throw new IllegalArgumentException("Argument could be only + or -");
        }
    }

    private static char[] getAlphabet() {
        char[] alphabet = new char[256];

        for (char i = 0; i < alphabet.length; i++) {
            alphabet[i] = i;
        }

        return alphabet;
    }

    private static int swapToFront(char[] array, char symbol) {
        if(array[0] == symbol) {
            return 0;
        }

        int count = 0;
        do {
            count++;
            char t = array[count - 1];
            array[count - 1] = array[count];
            array[count] = t;
        } while (symbol != array[count - 1]);

        return count;
    }
}
