import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;

import java.util.ArrayList;
import java.util.HashMap;

public class WordNet {
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if(synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }

        InitializeSynsets(synsets);
        InitializeHypernyms(hypernyms);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounesToIds.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if(word == null) {
            throw new IllegalArgumentException();
        }

        return nounesToIds.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if(!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }

        return new SAP(sunsetsGraph).length(nounesToIds.get(nounA), nounesToIds.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if(!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }

        return orderedSunsets[new SAP(sunsetsGraph).ancestor(nounesToIds.get(nounA), nounesToIds.get(nounB))];
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }

    private void InitializeSynsets(String synsets) {
        In synsetInput = new In(synsets);
        String[] lines = synsetInput.readAllLines();
        orderedSunsets = new String[lines.length];

        nounesToIds = new HashMap<>();

        for (String line : lines) {
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);

            orderedSunsets[id] = parts[1];

            String[] nouns = parts[1].split(" ");
            for (String noun : nouns) {
                ArrayList<Integer> ids = nounesToIds.getOrDefault(noun, null);
                if(ids == null) {
                    ArrayList<Integer> newBag = new ArrayList<>();
                    newBag.add(id);
                    nounesToIds.put(noun, newBag);
                }
                else {
                    ids.add(id);
                }
            }
        }

        sunsetsGraph = new Digraph(lines.length);
    }

    private void InitializeHypernyms(String hypernyms) {
        In hypernymsInput = new In(hypernyms);
        String[] lines = hypernymsInput.readAllLines();
        for (String line : lines) {
            String[] parts = line.split(",");
            int vertexId = Integer.parseInt(parts[0]);
            for(int i = 1; i < parts.length; i++) {
                int otherVertexId = Integer.parseInt(parts[i]);
                sunsetsGraph.addEdge(vertexId, otherVertexId);
            }
        }
    }

    private Digraph sunsetsGraph;
    private String[] orderedSunsets;
    private HashMap<String, ArrayList<Integer>> nounesToIds;
}