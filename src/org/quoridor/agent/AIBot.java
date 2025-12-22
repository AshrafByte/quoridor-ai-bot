package org.quoridor.agent;

import org.quoridor.evalutionFunction.EvaluationFunction;
import org.quoridor.evalutionFunction.PathLengthEvaluation;
import org.quoridor.searchStrategy.MinimaxSearch;
import org.quoridor.searchStrategy.SearchStrategy;
import org.quoridor.board.Board;
import org.quoridor.board.model.Move;

import java.util.List;
import java.util.Random;

public final class AIBot {
    private final int playerId;
    private int depth;
    private final SearchStrategy search;
    private final EvaluationFunction eval;
    private final Random rng = new Random();
    private final String difficulty;

    public AIBot(int playerId, String difficulty) {
        this.playerId = playerId;
        this.search = new MinimaxSearch();
        this.eval = new PathLengthEvaluation();
        this.difficulty = difficulty.toLowerCase();
        
        this.depth = switch (this.difficulty) {
            case "easy"   -> 1; // Random/Greedy
            case "medium" -> 2; // Basic Strategy
            case "hard"   -> 3; // Deep Strategy (Looks 3 moves ahead)
            default       -> 2;
        };
    }

    public Move chooseMove(Board board) {
        List<Move> legal = board.getLegalMoves(playerId);
        if (legal.isEmpty()) return null;

        // Easy Mode: 30% chance to make a random bad move
        if ("easy".equals(difficulty) && rng.nextDouble() < 0.3) {
            return legal.get(rng.nextInt(legal.size()));
        }

        return search.chooseMove(board, playerId, depth, eval);
    }
}
