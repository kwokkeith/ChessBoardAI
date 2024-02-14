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
        if ((0 < moveToTest.x2 && moveToTest.x2 < this.width) &
            (0 < moveToTest.y2 && moveToTest.y2 < this.height)) {
            return false;
        }
        return true;
    }

    private boolean is_move_blocked(Move moveToTest) {
        char finalSquare = current_state.board[moveToTest.x2][moveToTest.y2];
        if (finalSquare == EMPTY) {
            return false;
        }
        return true;
    }

    private boolean can_diagonal_move_capture(Move moveToTest, char opponent) {
        char finalSquare = current_state.board[moveToTest.x2][moveToTest.y2];
        if (finalSquare == opponent) {
            return true;
        }
        return false;
    }

    private void get_legal_moves_from_position(State state, ArrayList<Move> moves, int y, int x) {
        // x and y are current x and y coordinates
        // moves are possible moves

        // Find which pieces are enemy pieces - for capturing
        char opponent = state.white_turn ? BLACK : WHITE;

        int[][] hypotheticalMoves = {
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
                        moves.add(moveToAdd);
                    }
                } else if (!is_move_blocked(moveToAdd)) {
                    moves.add(moveToAdd);
                }
            }
        }
    }

    public ArrayList<Move> get_legal_moves_in_all_positions(State state) {
        ArrayList<Move> moves = new ArrayList<>();

        // Find which pieces are friendly pieces
        char friendly = state.white_turn ? WHITE : BLACK;

        // Iterate through board to find friendly pieces
        for (int y = 0; y < this.height; y++){
            for (int x = 0; x < this.width; x++) {
                // If the current position is friendly
                if (state.board[y][x] == friendly){
                    get_legal_moves_from_position(state, moves, y, x);
                }
            }
        }
        return moves;        
    }
    
    public void move(State state, Move move) {
        state.board[move.y2][move.x2] = state.board[move.y1][move.x1];
        // Old position becomes EMPTY 
        state.board[move.y1][move.x1] = EMPTY;
        // Flip the turn
        state.white_turn = !state.white_turn;
    }

    public void undo_move(State state, Move previousMove) {
        if (previousMove.is_diagonal()) {
            // Take new position and move it back to old position
            state.board[previousMove.y1][previousMove.x1] = state.board[previousMove.y2][previousMove.x2];

            // Check if new position is white or black depending on the current turn
            state.board[previousMove.y2][previousMove.x2] = state.white_turn ? WHITE : BLACK;
        } else {
            // Not diagonal move
            char tmp = state.board[previousMove.y1][previousMove.x1];
            state.board[previousMove.y1][previousMove.x1] = state.board[previousMove.y2][previousMove.x2];
            // Reset the next move to be the original decided move
            state.board[previousMove.y2][previousMove.x2] = tmp; 
        }

        state.white_turn = !state.white_turn;
    }
}
