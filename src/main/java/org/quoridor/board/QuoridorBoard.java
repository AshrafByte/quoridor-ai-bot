
package org.quoridor.board;

import org.quoridor.board.model.Move;
import org.quoridor.board.model.MoveKind;
import org.quoridor.board.model.Pos;
import org.quoridor.board.model.WallOrientation;

import java.util.*;

/**
 * 2-player Quoridor board implementing Board.
 * 9x9 grid, players start at (0,4) and (8,4).
 */

public final class QuoridorBoard implements Board {

    public static final int SIZE = 9;

    private final Pos p1Pos;
    private final Pos p2Pos;
    private final int p1Walls;
    private final int p2Walls;
    private final Set<Pos> wallsH; // horizontal walls at (row,col)
    private final Set<Pos> wallsV; // vertical walls at (row,col)
    private final int toMove;      // 1 or 2

    public QuoridorBoard() {
        this(new Pos(0, 4), new Pos(8, 4), 10, 10, new HashSet<>(), new HashSet<>(), 1);
    }

    private QuoridorBoard(Pos p1Pos,
                          Pos p2Pos,
                          int p1Walls,
                          int p2Walls,
                          Set<Pos> wallsH,
                          Set<Pos> wallsV,
                          int toMove) {
        this.p1Pos = p1Pos;
        this.p2Pos = p2Pos;
        this.p1Walls = p1Walls;
        this.p2Walls = p2Walls;
        this.wallsH = Collections.unmodifiableSet(wallsH);
        this.wallsV = Collections.unmodifiableSet(wallsV);
        this.toMove = toMove;
    }

    private QuoridorBoard copyWith(Pos p1Pos,
                                   Pos p2Pos,
                                   int p1Walls,
                                   int p2Walls,
                                   Set<Pos> wallsH,
                                   Set<Pos> wallsV,
                                   int toMove) {
        return new QuoridorBoard(p1Pos, p2Pos, p1Walls, p2Walls, wallsH, wallsV, toMove);
    }

    private int otherPlayer(int pid)    { return pid == 1 ? 2 : 1; }
    private Pos posOf(int pid)          { return pid == 1 ? p1Pos : p2Pos; }
    private int goalRow(int pid)        { return pid == 1 ? 8 : 0; }
    private int wallsLeft(int pid)      { return pid == 1 ? p1Walls : p2Walls; }

    // ---------------- Board interface ----------------

    @Override
    public boolean isTerminal() {
        return p1Pos.row() == 8 || p2Pos.row() == 0;
    }

    @Override
    public Integer getWinner() {
        boolean p1Goal = p1Pos.row() == 8;
        boolean p2Goal = p2Pos.row() == 0;
        if (p1Goal && p2Goal) return null;
        if (p1Goal) return 1;
        if (p2Goal) return 2;
        return null;
    }

    @Override
    public List<Move> getLegalMoves(int playerId) {
        if (isTerminal()) return List.of();

        List<Move> moves = new ArrayList<>();

        // Pawn moves
        for (Pos t : legalPawnTargets(playerId)) {
            moves.add(Move.pawn(t.row(), t.col()));
        }

        // Wall moves
        if (wallsLeft(playerId) > 0) {
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Pos cell = new Pos(r, c);
                    if (isWallPlacementValid(playerId, cell, WallOrientation.HORIZONTAL)) {
                        moves.add(Move.wall(r, c, WallOrientation.HORIZONTAL));
                    }
                    if (isWallPlacementValid(playerId, cell, WallOrientation.VERTICAL)) {
                        moves.add(Move.wall(r, c, WallOrientation.VERTICAL));
                    }
                }
            }
        }

        return moves;
    }

    @Override
    public Board applyMove(Move move) {
        MoveKind kind = move.kind();
        Pos target = new Pos(move.row(), move.col());

        Pos newP1 = p1Pos;
        Pos newP2 = p2Pos;
        int newP1Walls = p1Walls;
        int newP2Walls = p2Walls;
        Set<Pos> newWallsH = new HashSet<>(wallsH);
        Set<Pos> newWallsV = new HashSet<>(wallsV);
        int nextToMove = otherPlayer(toMove);

        if (kind == MoveKind.PAWN) {
            if (toMove == 1) newP1 = target;
            else newP2 = target;
        } else if (kind == MoveKind.WALL) {
            int pid = toMove;
            WallOrientation o = move.orientation();
            if (!isWallPlacementValid(pid, target, o)) {
                throw new IllegalArgumentException("Illegal wall placement " + move);
            }
            if (pid == 1) newP1Walls--;
            else newP2Walls--;

            if (o == WallOrientation.HORIZONTAL) newWallsH.add(target);
            else newWallsV.add(target);
        } else {
            throw new IllegalStateException("Unknown move kind: " + kind);
        }

        return copyWith(newP1, newP2, newP1Walls, newP2Walls, newWallsH, newWallsV, nextToMove);
    }

    @Override
    public int shortestPathLength(int playerId) {
        Pos start = posOf(playerId);
        int goal = goalRow(playerId);

        record Node(Pos pos, int dist) {}

        Queue<Node> q = new ArrayDeque<>();
        boolean[][] visited = new boolean[SIZE][SIZE];
        q.add(new Node(start, 0));
        visited[start.row()][start.col()] = true;

        while (!q.isEmpty()) {
            Node cur = q.remove();
            if (cur.pos().row() == goal) return cur.dist();

            for (Pos nb : neighbors4(cur.pos())) {
                if (visited[nb.row()][nb.col()]) continue;
                if (isEdgeBlocked(cur.pos(), nb)) continue;
                visited[nb.row()][nb.col()] = true;
                q.add(new Node(nb, cur.dist() + 1));
            }
        }

        return Integer.MAX_VALUE;
    }

    @Override
    public int getToMove() {
        return toMove;
    }
    // ---------------- Pawn movement helpers ----------------

    private List<Pos> neighbors4(Pos p) {
        int r = p.row(), c = p.col();
        List<Pos> res = new ArrayList<>(4);
        if (r > 0) res.add(new Pos(r - 1, c));
        if (r < SIZE - 1) res.add(new Pos(r + 1, c));
        if (c > 0) res.add(new Pos(r, c - 1));
        if (c < SIZE - 1) res.add(new Pos(r, c + 1));
        return res;
    }

    private boolean isEdgeBlocked(Pos a, Pos b) {
        int r1 = a.row(), c1 = a.col();
        int r2 = b.row(), c2 = b.col();

        if (Math.abs(r1 - r2) + Math.abs(c1 - c2) != 1) {
            return true; // not orthogonally adjacent
        }

        // Vertical move
        if (c1 == c2) {
            int rMin = Math.min(r1, r2);
            Pos h1 = new Pos(rMin, c1);
            if (wallsH.contains(h1)) return true;
            if (c1 > 0 && wallsH.contains(new Pos(rMin, c1 - 1))) return true;
            return false;
        }

        // Horizontal move
        int r = r1;
        int cMin = Math.min(c1, c2);
        Pos v1 = new Pos(r, cMin);
        if (wallsV.contains(v1)) return true;
        if (r > 0 && wallsV.contains(new Pos(r - 1, cMin))) return true;
        return false;
    }

    private List<Pos> legalPawnTargets(int pid) {
        Pos my = posOf(pid);
        Pos opp = posOf(otherPlayer(pid));
        Set<Pos> result = new LinkedHashSet<>();

        int[][] dirs = { {-1,0}, {1,0}, {0,-1}, {0,1} };

        for (int[] d : dirs) {
            int nr = my.row() + d[0];
            int nc = my.col() + d[1];
            if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) continue;
            Pos next = new Pos(nr, nc);
            if (isEdgeBlocked(my, next)) continue;

            if (!next.equals(opp)) {
                result.add(next);
            } else {
                // opponent adjacent: jump or diagonal-around
                int j2r = nr + d[0];
                int j2c = nc + d[1];
                Pos jumpTarget = new Pos(j2r, j2c);

                if (j2r >= 0 && j2r < SIZE && j2c >= 0 && j2c < SIZE &&
                        !isEdgeBlocked(next, jumpTarget)) {
                    result.add(jumpTarget);
                } else {
                    if (d[0] != 0) { // vertical move: diagonals left/right
                        int[] sideCols = { nc - 1, nc + 1 };
                        for (int sc : sideCols) {
                            if (sc < 0 || sc >= SIZE) continue;
                            Pos diag = new Pos(nr, sc);
                            if (!isEdgeBlocked(next, diag)) {
                                result.add(diag);
                            }
                        }
                    } else { // horizontal move: diagonals up/down
                        int[] sideRows = { nr - 1, nr + 1 };
                        for (int sr : sideRows) {
                            if (sr < 0 || sr >= SIZE) continue;
                            Pos diag = new Pos(sr, nc);
                            if (!isEdgeBlocked(next, diag)) {
                                result.add(diag);
                            }
                        }
                    }
                }
            }
        }

        return new ArrayList<>(result);
    }

    // ---------------- Wall placement ----------------

    private boolean isWallPlacementValid(int pid, Pos cell, WallOrientation o) {
        if (wallsLeft(pid) <= 0) return false;
        int r = cell.row(), c = cell.col();
        if (r < 0 || r > 7 || c < 0 || c > 7) return false;

        if (o == WallOrientation.HORIZONTAL) {
            if (wallsH.contains(cell)) return false;
            if (wallsH.contains(new Pos(r, c - 1)) || wallsH.contains(new Pos(r, c + 1)))
                return false;
            if (wallsV.contains(cell)) return false; // crossing
        } else {
            if (wallsV.contains(cell)) return false;
            if (wallsV.contains(new Pos(r - 1, c)) || wallsV.contains(new Pos(r + 1, c)))
                return false;
            if (wallsH.contains(cell)) return false; // crossing
        }

        // simulate placement and ensure both paths exist
        Set<Pos> tmpH = new HashSet<>(wallsH);
        Set<Pos> tmpV = new HashSet<>(wallsV);
        if (o == WallOrientation.HORIZONTAL) tmpH.add(cell);
        else tmpV.add(cell);

        QuoridorBoard tmp = copyWith(p1Pos, p2Pos, p1Walls, p2Walls, tmpH, tmpV, toMove);
        int d1 = tmp.shortestPathLength(1);
        int d2 = tmp.shortestPathLength(2);
        if (d1 == Integer.MAX_VALUE || d2 == Integer.MAX_VALUE) return false;

        return true;
    }

    // ---------------- Getters (useful for GUI/debug) ----------------

    public Pos getP1Pos() { return p1Pos; }
    public Pos getP2Pos() { return p2Pos; }
    public int getP1Walls() { return p1Walls; }
    public int getP2Walls() { return p2Walls; }
}