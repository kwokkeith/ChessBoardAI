import java.util.ArrayList;

public class Environment {
    public State current_state;
    public int width, height;
    static final char BLACK = 'B';
    static final char WHITE = 'W';
    static final char EMPTY = ' ';

    /**
     * Internal "board" 
     * @param width Board's width. Will not change. [0, width)
     * @param height Board's height. Will not change. [0, height)
     */
    public Environment(int width, int height) {
        this.width = width;
        this.height = height;
        this.current_state = new State(width, height);
    }

    /**
     * Checks if a Move's final position is in the board; whether move is valid
     * @param moveToTest A Move object. We will extract out x2 and y2 from it.
     * @return false if it is not out-of-bounds; true if it is
     */
    private boolean is_move_out_of_bounds(Move moveToTest) {
        
        if ((0 <= moveToTest.x1 && moveToTest.x1 < this.width) &
            (0 <= moveToTest.y1 && moveToTest.y1 < this.height) &
            (0 <= moveToTest.x2 && moveToTest.x2 < this.width) &
            (0 <= moveToTest.y2 && moveToTest.y2 < this.height)) {
            return false;
        }
        return true;
    }

    /**
     * Checks for whether the final position is empty or not (blocked or not)
     * @param moveToTest We will extract out the x2 and y2 (final position) from it
     * @return false if it is empty; true if it is not empty
     */
    private boolean is_move_blocked(Move moveToTest) {
        char finalSquare = current_state.board[moveToTest.x2][moveToTest.y2];
        if (finalSquare == EMPTY) {
            return false;
        }
        return true;
    }

    /**
     * Check for whether there is a capturable-diagonal piece from current position
     * @param moveToTest This should be already a diagonal move itself.
     * @param opponent We need to check if the opponent is on the final position of this move
     * @return true if there is an opponent in the final position; false if not
     */
    private boolean can_diagonal_move_capture(Move moveToTest, char opponent) {
        char finalSquare = current_state.board[moveToTest.x2][moveToTest.y2];
        if (finalSquare == opponent) {
            return true;
        }
        return false;
    }

    /**
     * Helper function for get_legal_moves_in_all_positions
     * @param state state of the board. Used to figure whose turn it is
     * @param x x coordinate of current position
     * @param y y coordinate of current position
     * @return ArrayList<Move> moves that are possible from current position
     */
    public ArrayList<Move> get_legal_moves_from_position(State state, int x, int y) {
        System.out.println("XY : " + String.valueOf(x) + String.valueOf(y));
        // Find which pieces are enemy pieces - for capturing
        char opponent = state.white_turn ? BLACK : WHITE;
        ArrayList<Move> legalMoves = new ArrayList<Move>();

        int[][] hypotheticalMoves = {
            // {X,Y}
            // This will show all possible moves; for all directions, including diagonal moves.
            // The checks for whether the moves are possible are below
            {-1, 2}, {1, 2}, {-2, 1}, {2, 1}, 
            {-1, -2}, {1, -2}, {-2, -1}, {2, -1},
            {-1, 1}, {1, 1}, {-1, -1}, {1, -1}
        };
        
        for (int[] hypotheticalMove : hypotheticalMoves) {
            int newX = x + hypotheticalMove[0];
            int newY = y + hypotheticalMove[1];
            Move moveToAdd = new Move(x, y, newX, newY);
            if (!is_move_out_of_bounds(moveToAdd)) {
                if (moveToAdd.is_diagonal()) {
                    if (can_diagonal_move_capture(moveToAdd, opponent)) {
                        
                        System.out.println("Move can diagonally capture!: " + moveToAdd.toString());
                        legalMoves.add(moveToAdd);
                    }
                } else if (!is_move_blocked(moveToAdd)) {
                    System.out.println("Move is validly to empty spot!");
                    legalMoves.add(moveToAdd);
                }
            }
        }
        return legalMoves;
    }

    /**
     * Iterate through entire board and compute all legal moves from each position in board.
     * @param state the state of the board. Used to figure whether a piece is friendly or not
     * @return ArrayList<Move> all moves that are possible from current position
     */
    public ArrayList<Move> get_legal_moves_in_all_positions(State state) {
        ArrayList<Move> moves = new ArrayList<>();

        // Find which pieces are friendly pieces - rationale is to move own's piece only
        char friendly = state.white_turn ? WHITE : BLACK;

        for (int y = 0; y < this.height; y++){
            for (int x = 0; x < this.width; x++) {
                System.out.println("X and Y iterated in get_legal moves in all positions");
                if (state.board[x][y] == friendly) {
                    for (Move legalMove : get_legal_moves_from_position(state, x, y)) {
                        moves.add(legalMove);
                    }
                }
            }
        }
        return moves;        
    }
    
    public void move(State state, Move move) {
        state.board[move.x2][move.y2] = state.board[move.x1][move.y1];
        // Old position becomes EMPTY 
        System.out.println(state);
        state.board[move.x1][move.y1] = EMPTY;
        // After the move, pass on the turn
        state.white_turn = !state.white_turn;
    }

    public void undo_move(State state, Move previousMove) {
        if (previousMove.is_diagonal()) {
            // Previous move captured the diagonal horse.
            // Take new position and move it back to old position
            state.board[previousMove.x1][previousMove.y1] = state.board[previousMove.x2][previousMove.y2];
            // Set back the opponent's piece that was captured.
            state.board[previousMove.x2][previousMove.y2] = state.white_turn ? WHITE : BLACK;
        } else {
            // Not diagonal move; just a standard empty move.
            char tmp = state.board[previousMove.x1][previousMove.y1];
            state.board[previousMove.x1][previousMove.y1] = state.board[previousMove.x2][previousMove.y2];
            // Reset the next move to be the original decided move
            state.board[previousMove.x2][previousMove.y2] = tmp; 
        }

        // Pass turn
        state.white_turn = !state.white_turn;
    }
}
