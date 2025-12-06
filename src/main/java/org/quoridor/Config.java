package org.quoridor;

import org.quoridor.board.model.Pos;
import java.util.List;

public final class Config {
    private final int boardSize;
    private final int numPlayers;
    private final int initialWallsPerPlayer;
    private final List<Pos> startingPositions;
    private final int maxSearchDepth;
    private final int timeLimitMillis;


    public Config() {
        this.boardSize = 9;
        this.numPlayers = 2;
        this.initialWallsPerPlayer = 10;
        this.startingPositions = List.of(new Pos(0, 4), new Pos(8, 4));
         this.maxSearchDepth = 4;
         this.timeLimitMillis = 5000;
    }
    public Config(int boardSize, int numPlayers, int initialWallsPerPlayer,
                          List<Pos> startingPositions, int maxSearchDepth, int timeLimitMillis)
    {
        this.boardSize = boardSize;
        this.numPlayers = numPlayers;
         this.initialWallsPerPlayer = initialWallsPerPlayer;
        this.startingPositions = startingPositions;
        this.maxSearchDepth = maxSearchDepth;
        this.timeLimitMillis = timeLimitMillis;
    }

    public int getBoardSize() { return boardSize; }
    public int getNumPlayers() { return numPlayers; }
    public int getInitialWallsPerPlayer() { return initialWallsPerPlayer; }
    public List<Pos> getStartingPositions() { return startingPositions; }
    public int getMaxSearchDepth() { return maxSearchDepth; }
    public int getTimeLimitMillis() { return timeLimitMillis; }
}