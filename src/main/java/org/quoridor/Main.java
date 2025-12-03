package org.quoridor;


import org.quoridor.agent.AIBot;
import org.quoridor.board.QuoridorBoard;
import org.quoridor.board.Board;
import org.quoridor.board.model.Move;

public class Main {
    public static void main(String[] args) {
        Board board = new QuoridorBoard();
        AIBot ai1 = new AIBot(1, "easy");
        AIBot ai2 = new AIBot(2, "easy");

        QuoridorBoard qb = (QuoridorBoard) board;
        System.out.println("Initial positions: P1 " + qb.getP1Pos()
                + " P2 " + qb.getP2Pos());

        int moveCount = 0;
        final int maxMoves = 500;

        while (!board.isTerminal() && moveCount < maxMoves) {
            int pid = board.getToMove();               // now via interface
            AIBot ai = (pid == 1) ? ai1 : ai2;

            Move move = ai.chooseMove(board);
            if (move == null) {
                System.out.println("Player " + pid + " has no legal moves.");
                break;
            }

            System.out.println("Player " + pid + " plays: " + move);
            board = board.applyMove(move);

            qb = (QuoridorBoard) board;
            System.out.println("   P1: " + qb.getP1Pos() + " walls: " + qb.getP1Walls()
                    + " | P2: " + qb.getP2Pos() + " walls: " + qb.getP2Walls());

            moveCount++;
        }

        System.out.println("Game ended. Winner: " + board.getWinner());
    }
}
