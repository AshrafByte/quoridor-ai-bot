package org.quoridor.board.model;


public record Move(
        MoveKind kind,
        int row,
        int col,
        WallOrientation orientation // null for pawn moves
) {
    public static Move pawn(int row, int col) {
        return new Move(MoveKind.PAWN, row, col, null);
    }

    public static Move wall(int row, int col, WallOrientation orientation) {
        return new Move(MoveKind.WALL, row, col, orientation);
    }

    @Override
    public String toString() {
        return switch (kind) {
            case PAWN -> "Pawn -> (" + row + "," + col + ")";
            case WALL -> "Wall " + orientation + " @ (" + row + "," + col + ")";
        };
    }
}
