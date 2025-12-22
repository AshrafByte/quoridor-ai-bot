package org.quoridor.evalutionFunction;

import org.quoridor.board.Board;

public interface EvaluationFunction {
    double evaluate(Board state, int playerId, int opponentId);
}

