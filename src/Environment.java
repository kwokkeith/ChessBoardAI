import java.util.ArrayList;

public class Environment {
    public State current_state;
    public int width, height;
    static final char BLACK = 'B';
    static final char WHITE = 'W';
    static final char EMPTY = ' ';

    public Environment(int width, int height) {
        this.width = width;
        this.height = height;
        this.current_state = new State(width, height);
    }

    private boolean can_move_n_steps_forward(State state, int y, int max_height_black, int max_height_white){
        if (state.white_turn && y <= max_height_white) return true;
        if (!state.white_turn && y >= max_height_black) return true;
        return false;
    }

    private void get_moves(State state, ArrayList<Move> moves, int y, int x){
        // Find which pieces are enemy pieces
        char opponent = state.white_turn ? BLACK : WHITE;

        // Constants for number of steps
        int one_step = state.white_turn ? 1 : -1;
        int two_steps = state.white_turn ? 2 : -2;

        // Two steps forward and one step left/right
        // Check if the above move would be within bounds of the board
        if (can_move_n_steps_forward(state, y, 2, this.height - 3)){
            // LEFT
            if (x > 0 && state.board[y + two_steps][x - 1] == EMPTY)
                moves.add(new Move(x, y, x - 1, y + two_steps));
            
            // RIGHT
            if (x < this.width - 1 && state.board[y + two_steps][x + 1] == EMPTY)
                moves.add(new Move(x, y, x + 1, y + two_steps));
        }

        // One step forward and two steps left/right
        if (can_move_n_steps_forward(state, y, 1, this.height - 2)){
            // LEFT
            if (x > 1 && state.board[y + one_step][x - 2] == EMPTY)
                moves.add(new Move(x, y, x - 2, y + one_step));
            
            // RIGHT
            if (x < this.width - 2 && state.board[y + one_step][x + 2] == EMPTY)
                moves.add(new Move(x, y, x + 2, y + one_step));
        }

        // Diagonal (Capture) - only if there is an opponent
        if (can_move_n_steps_forward(state, y, 1, this.height - 2)){
            // Diagonally LEFT
            if (x > 0 && state.board[y + one_step][x - 1] == opponent)
                moves.add(new Move(x, y, x - 1, y + one_step));
            
            // Diagonally RIGHT
            if (x < this.width - 1 && state.board[y + one_step][x + 1] == opponent)
                moves.add(new Move(x, y, x + 1, y + one_step));
        }
    }

    public ArrayList<Move> get_legal_moves(State state) {
        ArrayList<Move> moves = new ArrayList<>();

        // Find which pieces are friendly pieces
        char friendly = state.white_turn ? WHITE : BLACK;

        // Iterate through board to find friendly pieces
        for (int y = 0; y < this.height; y++){
            for (int x = 0; x < this.width; x++) {
                // If the current position is friendly
                if (state.board[y][x] == friendly){
                    get_moves(state, moves, y, x);
                }
            }
        }
        return moves;        
    }

    public void move(State state, Move move) {
        // Move to new position
        state.board[move.y2][move.x2] = state.board[move.y1][move.x1];

        // Old position becomes EMPTY 
        state.board[move.y1][move.x1] = EMPTY;

        // Flip the turn
        state.white_turn = !state.white_turn;
    }

    private boolean was_diagonal_move(Move move){
        // Checks if the current move is a diagonal move
        if (move.y2 - 1 == move.y1 && move.x2 - 1 == move.x1){
            return true;
        }
        if (move.y2 + 1 == move.y1 && move.x2 - 1 == move.x1){
            return true;
        }
        if (move.y2 - 1 == move.y1 && move.x2 + 1 == move.x1){
            return true;
        }
        if (move.y2 + 1 == move.y1 && move.x2 + 1 == move.x1){
            return true;
        }
        return false;
    }

    public void undo_move(State state, Move move) {
        if (was_diagonal_move(move)) {
            // Is diagonal move
            // Take new position and move it back to old position
            state.board[move.y1][move.x1] = state.board[move.y2][move.x2];

            // Check if new position is white or black depending on the current turn
            state.board[move.y2][move.x2] = state.white_turn ? WHITE : BLACK;
        } else {
            // Not diagonal move
            char tmp = state.board[move.y1][move.x1];
            state.board[move.y1][move.x1] = state.board[move.y2][move.x2];
            // Reset the next move to be the original decided move
            state.board[move.y2][move.x2] = tmp; 
        }

        state.white_turn = !state.white_turn;
    }
}
