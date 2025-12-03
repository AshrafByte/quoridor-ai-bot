package org.quoridor.board;

import org.quoridor.board.model.Move;

import java.util.List;

public interface Board {
    boolean isTerminal();
    Integer getWinner(); // 1, 2, or null

    List<Move> getLegalMoves(int playerId);
    Board applyMove(Move move);

    int shortestPathLength(int playerId); // use Integer.MAX_VALUE if no path

    int getToMove();
}
