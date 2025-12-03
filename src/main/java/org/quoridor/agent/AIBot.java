package org.quoridor.agent;

import org.quoridor.evalutionFunction.EvaluationFunction;
import org.quoridor.evalutionFunction.PathLengthEvaluation;
import org.quoridor.searchStrategy.MinimaxSearch;
import org.quoridor.searchStrategy.SearchStrategy;
import org.quoridor.board.Board;
import org.quoridor.board.model.Move;

import java.util.*;

public final class AIBot {
    private final int playerId;
    private String difficulty;
    private int depth;
    private final SearchStrategy search;
    private final EvaluationFunction eval;
    private final Random rng = new Random();

    public AIBot(int playerId) {
        this(playerId, "medium", new MinimaxSearch(), new PathLengthEvaluation());
    }

    public AIBot(int playerId, String difficulty) {
        this(playerId, difficulty, new MinimaxSearch(), new PathLengthEvaluation());
    }

    public AIBot(int playerId, String difficulty,
                 SearchStrategy search,
                 EvaluationFunction eval) {
        if (playerId != 1 && playerId != 2) {
            throw new IllegalArgumentException("playerId must be 1 or 2");
        }
        this.playerId = playerId;
        this.search = search;
        this.eval = eval;
        setDifficulty(difficulty);
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty.toLowerCase();
        this.depth = switch (this.difficulty) {
            case "easy"   -> 1;
            case "medium" -> 2;
            case "hard"   -> 3;
            default       -> 2;
        };
    }

    public Move chooseMove(Board board) {
        List<Move> legal = board.getLegalMoves(playerId);
        if (legal.isEmpty()) return null;

        if ("easy".equals(difficulty)) {
            return legal.get(rng.nextInt(legal.size()));
        }

        return search.chooseMove(board, playerId, depth, eval);
    }
}
