import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    public Outcast(WordNet wordnet) {         // constructor takes a WordNet object
        _wordnet = wordnet;
    }

    public String outcast(String[] nouns) {   // given an array of WordNet nouns, return an outcast
        int maxDistance = 0;
        int maxNounId = -1;

        for (int i = 0; i < nouns.length; i++) {
            int distance = 0;
            for (String noun : nouns) {
                distance += _wordnet.distance(noun, nouns[i]);
            }

            if(distance > maxDistance) {
                maxDistance = distance;
                maxNounId = i;
            }
        }

        return nouns[maxNounId];
    }

    public static void main(String[] args) {  // see test client below
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }

    private WordNet _wordnet;
}
