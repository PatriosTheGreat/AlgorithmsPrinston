import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseballElimination {
    public BaseballElimination(String filename) {                    // create a baseball division from given filename in format specified below
        In input = new In(filename);
        int teamsCount = Integer.parseInt(input.readLine());

        teams = new String[teamsCount];
        wins = new int[teamsCount];
        loses = new int[teamsCount];
        remainings = new int[teamsCount];
        remainGames = new int[teamsCount][teamsCount];
        team2Index = new HashMap<>();
        eliminators = new HashMap<>();

        for(int i = 0; i < teams.length; i++) {
            String[] parts = input.readLine().trim().split("\\s+");
            team2Index.put(parts[0], i);
            teams[i] = parts[0];
            wins[i] = Integer.parseInt(parts[1]);
            loses[i] = Integer.parseInt(parts[2]);
            remainings[i] = Integer.parseInt(parts[3]);

            for(int j = 0; j < teams.length; j++) {
                remainGames[i][j] = Integer.parseInt(parts[j + 4]);
            }
        }

        initializeEliminators();
    }

    public int numberOfTeams() {                        // number of teams
        return team2Index.size();
    }

    public Iterable<String> teams() {                   // all teams
        return team2Index.keySet();
    }

    public int wins(String team) {                      // number of wins for given team
        checkTeam(team);

        return wins[team2Index.get(team)];
    }

    public int losses(String team) {                    // number of losses for given team
        checkTeam(team);

        return loses[team2Index.get(team)];
    }

    public int remaining(String team) {                 // number of remaining games for given team
        checkTeam(team);

        return remainings[team2Index.get(team)];
    }

    public int against(String team1, String team2) {        // number of remaining games between team1 and team2
        checkTeam(team1);
        checkTeam(team2);

        return remainGames[team2Index.get(team1)][team2Index.get(team2)];
    }

    public boolean isEliminated(String team) {              // is given team eliminated?
        checkTeam(team);

        return eliminators.containsKey(team2Index.get(team));
    }

    public Iterable<String> certificateOfElimination(String team) {  // subset R of teams that eliminates given team; null if not eliminated
        checkTeam(team);

        return eliminators.get(team2Index.get(team));
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

    private void initializeEliminators() {
        for(int i = 0; i < teams.length; i++) {
            initializeEliminatorsForTeam(i);
        }
    }

    private void initializeEliminatorsForTeam(int teamId) {
        FlowNetwork flowNetwork = new FlowNetwork(networkSize());

        addRemainingEdges(flowNetwork, teamId);
        addTeamsEdges(flowNetwork, teamId);
        addEdgesToFinish(flowNetwork, teamId);

        FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, startNode, finishNode());
        for(int i = 0; i < teamId; i++) {
            if(fordFulkerson.inCut(getTeamNode(i))) {
                addEliminator(teamId, i);
            }
        }

        for(int i = teamId + 1; i < teams.length; i++) {
            if(fordFulkerson.inCut(getTeamNode(i) - 1)) {
                addEliminator(teamId, i);
            }
        }
    }

    private void addEliminator(int teamId, int eliminator) {
        if(eliminators.containsKey(teamId)) {
            ArrayList<String> eliminatorForTeam = eliminators.get(teamId);
            if(!eliminatorForTeam.contains(teams[eliminator])) {
                eliminatorForTeam.add(teams[eliminator]);
            }
        }
        else {
            ArrayList<String> eliminatorTeams = new ArrayList<>();
            eliminatorTeams.add(teams[eliminator]);
            eliminators.put(teamId, eliminatorTeams);
        }
    }

    private void addEdgesToFinish(FlowNetwork flowNetwork, int teamId) {
        int possibility = wins[teamId] + remainings[teamId];
        for(int i = 0; i < teamId; i++) {
            if (possibility > wins[i]) {
                flowNetwork.addEdge(new FlowEdge(getTeamNode(i), finishNode(), possibility - wins[i]));
            } else {
                addEliminator(teamId, i);
            }
        }

        for(int i = teamId + 1; i < teams.length; i++) {
            if (possibility > wins[i]) {
                flowNetwork.addEdge(new FlowEdge(getTeamNode(i - 1), finishNode(), possibility - wins[i]));
            } else {
                addEliminator(teamId, i);
            }
        }
    }

    private void addTeamsEdges(FlowNetwork flowNetwork, int teamId) {
        int matrixSize = (remainGames.length - 1);
        int upperCount = (matrixSize * (matrixSize - 1) / 2);
        for(int firstTeamIndex = 0; firstTeamIndex < remainGames.length; firstTeamIndex++) {
            for (int secondTeamIndex = 0; secondTeamIndex < remainGames.length; secondTeamIndex++) {
                if (secondTeamIndex < firstTeamIndex || firstTeamIndex == teamId || secondTeamIndex == teamId || firstTeamIndex == secondTeamIndex) {
                    continue;
                }

                int firstTeam = firstTeamIndex;
                if(firstTeamIndex > teamId) {
                    firstTeam--;
                }

                int secondTeam = secondTeamIndex;
                if(secondTeamIndex > teamId) {
                    secondTeam--;
                }

                int firstNode = getTeamNode(firstTeam);
                int secondNode = getTeamNode(secondTeam);

                int index = upperCount - (matrixSize - firstTeam) * (matrixSize - firstTeam - 1) / 2 + secondTeam - firstTeam - 1;
                int gameNode = getRemainingNode(index);
                flowNetwork.addEdge(new FlowEdge(gameNode, firstNode, Double.POSITIVE_INFINITY));
                flowNetwork.addEdge(new FlowEdge(gameNode, secondNode, Double.POSITIVE_INFINITY));
            }
        }
    }

    private void addRemainingEdges(FlowNetwork flowNetwork, int teamId) {
        int node = 0;
        for(int firstTeamIndex = 0; firstTeamIndex < remainGames.length; firstTeamIndex++) {
            for (int secondTeamIndex = 0; secondTeamIndex < remainGames.length; secondTeamIndex++) {
                if (secondTeamIndex < firstTeamIndex || firstTeamIndex == teamId || secondTeamIndex == teamId || firstTeamIndex == secondTeamIndex) {
                    continue;
                }

                flowNetwork.addEdge(
                    new FlowEdge(startNode, getRemainingNode(node), remainGames[firstTeamIndex][secondTeamIndex]));

                node++;
            }
        }
    }

    private int getTeamNode(int teamNumber) {
        return teamsNodesStart() + teamNumber;
    }

    private int getRemainingNode(int remainingNumber) {
        return remainingNodesStart + remainingNumber;
    }

    private void checkTeam(String team) {
        if(!team2Index.containsKey(team)) {
            throw new IllegalArgumentException();
        }
    }

    private int networkSize() {
        if(networkSizeCache == -1) {
            networkSizeCache = remainsNodesCount() + teamsNodesCount() + 2;
        }

        return networkSizeCache;
    }

    private int remainsNodesCount() {
        if(remainsNodesCountCache == -1) {
            remainsNodesCountCache = ((int)Math.ceil((remainings.length - 1) * (remainings.length - 2) / 2));
        }

        return remainsNodesCountCache;
    }

    private int teamsNodesStart() {
        return remainingNodesStart + remainsNodesCount();
    }

    private int teamsNodesCount() {
        return teams.length - 1;
    }

    private int finishNode() {
        return networkSize() - 1;
    }

    private int networkSizeCache = -1;
    private int remainsNodesCountCache = -1;
    private final int startNode = 0;
    private final int remainingNodesStart = 1;
    private HashMap<Integer, ArrayList<String>> eliminators;
    private final HashMap<String, Integer> team2Index;
    private final String[] teams;
    private final int[] wins;
    private final int[] loses;
    private final int[] remainings;
    private final int[][] remainGames;
}