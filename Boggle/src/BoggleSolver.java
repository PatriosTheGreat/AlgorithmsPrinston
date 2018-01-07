import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BoggleSolver {

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        tree = new PrefixTree();

        for (String word : dictionary) {
            if(word.length() > 2) {
                tree.add(word, getWordScore(word));
            }
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if(board == null) {
            throw new IllegalArgumentException();
        }

        HashSet<String> foundWord = new HashSet<>();

        for(int row = 0; row < board.rows(); row++) {
            for(int col = 0; col < board.cols(); col++) {
                for (String word : new WordsCollector(board, tree, row, col).words()) {
                    if(!foundWord.contains(word)) {
                        foundWord.add(word);
                    }
                }
            }
        }

        return foundWord;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if(word == null || word.length() == 0) {
            throw new IllegalArgumentException();
        }

        return tree.get(word);
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

    private int getWordScore(String word) {
        if(word.length() <= 2) {
            return 0;
        }

        if(word.length() <= 4) {
            return 1;
        }

        if(word.length() <= 5) {
            return 2;
        }

        if(word.length() <= 6) {
            return 3;
        }

        if(word.length() <= 7) {
            return 5;
        }

        return 11;
    }

    private final PrefixTree tree;

    private class WordsCollector {
        public WordsCollector(BoggleBoard boggle, PrefixTree t, int row, int col) {
            board = boggle;
            tree = t;
            marks = new boolean[boggle.rows()][boggle.cols()];
            marks[row][col] = true;
            words = new ArrayList<>();
            collect(charToString(board.getLetter(row, col)), row, col);
        }

        public Iterable<String> words() {
            return words;
        }

        private void collect(String currentWord, int row, int col) {
            if(currentWord.length() > tree.maxWordLength()) {
                return;
            }

            if(tree.get(currentWord) != 0) {
                words.add(currentWord);
            }

            collectNeighbor(currentWord, row - 1, col - 1);
            collectNeighbor(currentWord, row - 1, col);
            collectNeighbor(currentWord, row - 1, col + 1);
            collectNeighbor(currentWord,row, col - 1);
            collectNeighbor(currentWord,row, col + 1);
            collectNeighbor(currentWord,row + 1, col - 1);
            collectNeighbor(currentWord,row + 1, col);
            collectNeighbor(currentWord,row + 1, col + 1);
        }

        private void collectNeighbor(String currentWord, int row, int col) {
            if(row < 0 || row >= board.rows() || col < 0 || col >= board.cols()) {
                return;
            }

            if(marks[row][col]) {
                return;
            }

            if(!tree.containsPrefix(currentWord)) {
                return;
            }

            marks[row][col] = true;
            collect(currentWord + charToString(board.getLetter(row, col)), row, col);
            marks[row][col] = false;
        }

        private String charToString(char c) {
            return c == 'Q' ? "QU" : "" + c;
        }

        private ArrayList<String> words;
        private BoggleBoard board;
        private PrefixTree tree;
        private boolean[][] marks;
    }

    private class PrefixTree {
        public PrefixTree() {
            root = new PrefixTreeNode();
        }

        public void add(String key, int value) {
            if(key.length() > maxLength) {
                maxLength = key.length();
            }

            NodePosition position = getPosition(key);
            PrefixTreeNode node = position.getNode();
            if(position.isInEdge()) {
                if(!position.isEdgeFullyChecked()) {
                    int nodePosition = node.getFrom() + position.getEdgePosition();

                    PrefixTreeNode oldEdge = new PrefixTreeNode(node.getValue(), nodePosition, node.getTo());

                    oldEdge.setNextNodes(node.nextNodes);
                    node.setNextNodes(new HashMap<>());

                    node.nextNodes().put(words.charAt(nodePosition), oldEdge);
                    node.setValue(ZeroValue);
                }

                if(position.isWordFound()) {
                    node.setValue(value);
                }
                else {
                    int from = wordsLength + position.getWordPosition();
                    int to = wordsLength + key.length() - 1;
                    PrefixTreeNode newEdge = new PrefixTreeNode(value, from, to);
                    node.nextNodes().put(key.charAt(position.getWordPosition()), newEdge);
                }

                node.setTo(node.getFrom() + position.getEdgePosition() - 1);
            }
            else {
                int from = wordsLength + position.getWordPosition();
                int to = wordsLength + key.length() - 1;
                PrefixTreeNode nextNode = new PrefixTreeNode(value, from, to);

                node.nextNodes().put(key.charAt(position.getWordPosition()), nextNode);
            }

            words.append(key);
            wordsLength += key.length();
        }

        public int get(String key) {
            NodePosition position = getPosition(key);
            return position.isEdgeFullyChecked() && position.isWordFound() ? position.getNode().value : ZeroValue;
        }

        public int maxWordLength() {
            return maxLength;
        }

        public boolean containsPrefix(String prefix) {
            NodePosition position = getPosition(prefix);
            return position.isInEdge() && position.isWordFound();
        }

        private NodePosition getPosition(String word) {
            PrefixTreeNode currentNode = root;
            int wordPosition;
            int nodePosition = -1;
            for(wordPosition = 0; wordPosition < word.length();) {
                PrefixTreeNode nextNode = currentNode.nextNodes().getOrDefault(word.charAt(wordPosition), null);
                if (nextNode == null) {
                    return new NodePosition(currentNode, -1, wordPosition, word);
                }
                else {
                    for(nodePosition = nextNode.getFrom();
                        nodePosition <= nextNode.getTo() && wordPosition < word.length() && words.charAt(nodePosition) == word.charAt(wordPosition);
                        nodePosition++, wordPosition++);

                    boolean isEdgeChecked = (nodePosition - 1) == nextNode.getTo();

                    if(wordPosition == word.length()) {
                        return new NodePosition(nextNode, nodePosition, wordPosition, word);
                    }

                    if(isEdgeChecked) {
                        currentNode = nextNode;
                        continue;
                    }

                    return new NodePosition(nextNode, nodePosition, wordPosition, word);
                }
            }

            return new NodePosition(currentNode, nodePosition, wordPosition, word);
        }

        private final PrefixTreeNode root;
        private StringBuilder words = new StringBuilder();
        private int wordsLength;
        private static final int ZeroValue = 0;
        private int maxLength;

        private final class NodePosition {
            public NodePosition(PrefixTreeNode n, int edgePos, int wordPos, String w) {
                node = n;
                edgePosition = edgePos;
                wordPosition = wordPos;
                word = w;
            }

            public boolean isEdgeFullyChecked() {
                return (edgePosition - 1) == node.getTo();
            }

            public boolean isInEdge() {
                return edgePosition != -1;
            }

            public boolean isWordFound() {
                return word.length() == wordPosition;
            }

            public int getWordPosition() {
                return wordPosition;
            }

            public PrefixTreeNode getNode() {
                return node;
            }

            public int getEdgePosition() {
                return edgePosition - node.getFrom();
            }

            private final int edgePosition;
            private final int wordPosition;
            private final PrefixTreeNode node;
            private final String word;
        }

        private final class PrefixTreeNode {
            public PrefixTreeNode() {
                nextNodes = new HashMap<>();
            }

            public PrefixTreeNode(int value, int from, int to) {
                nextNodes = new HashMap<>();
                setValue(value);
                setFrom(from);
                setTo(to);
            }

            public int getValue() {
                return value;
            }

            public void setValue(int val) {
                value = val;
            }

            public HashMap<Character, PrefixTreeNode> nextNodes() {
                return nextNodes;
            }

            public void setNextNodes(HashMap<Character, PrefixTreeNode> nodes) {
                nextNodes = nodes;
            }

            public int getFrom() {
                return from;
            }

            public void setFrom(int value) {
                from = value;
            }

            public int getTo() {
                return to;
            }

            public void setTo(int value) {
                to = value;
            }

            private int from;
            private int to;
            private HashMap<Character, PrefixTreeNode> nextNodes;
            private int value = ZeroValue;
        }
    }
}