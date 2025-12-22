package org.quoridor.searchStrategy;

import org.quoridor.board.Board;
import org.quoridor.evalutionFunction.EvaluationFunction;
import org.quoridor.board.model.Move;

public interface SearchStrategy {
    Move chooseMove(Board board, int playerId, int depth, EvaluationFunction eval);
}