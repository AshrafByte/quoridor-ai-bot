package org.quoridor.evalutionFunction;


import org.quoridor.board.Board;

public final class PathLengthEvaluation implements EvaluationFunction {
    @Override
    public double evaluate(Board state, int playerId, int opponentId) {
        if (state.isTerminal()) {
            Integer w = state.getWinner();
            if (w == null) return 0.0;
            if (w == playerId) return Double.POSITIVE_INFINITY;
            if (w == opponentId) return Double.NEGATIVE_INFINITY;
            return 0.0;
        }

        int myDist = state.shortestPathLength(playerId);
        int oppDist = state.shortestPathLength(opponentId);

        if (myDist == Integer.MAX_VALUE && oppDist == Integer.MAX_VALUE) return 0.0;
        if (myDist == Integer.MAX_VALUE) return -1_000_000.0;
        if (oppDist == Integer.MAX_VALUE) return 1_000_000.0;

        return oppDist - myDist;
    }
}
