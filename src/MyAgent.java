import java.util.ArrayList;
import java.util.Arrays;

public class MyAgent implements Agent {
	public static long playclock; // this is how much time (in ms) we have before nextAction needs to return a move
	private boolean myTurn; // whether it is this agent's turn or not
    private Environment env; // To know about board environment

	/*
		init(String role, int playclock) is called once before you have to select the first action. Use it to initialize the agent. role is either "white" or "black" and playclock is the number of seconds after which nextAction must return.
	*/
    public void init(String role, int width, int height, int current_playclock) {
		playclock = current_playclock * 1000; 
		myTurn = !role.equals("white");

        this.env = new Environment(width, height);
    }


    /**
     * Computes the next best course of action. Then executes it
     * @param lastMove Coordinates of the previous move in the form (x1, y1, x2, y2). null on game start.
     */
    public String nextAction(int[] lastMoveCoordinates) {
        if (lastMoveCoordinates != null) {
            Move lastMove = new Move(lastMoveCoordinates[0]-1,lastMoveCoordinates[1]-1, lastMoveCoordinates[2]-1, lastMoveCoordinates[3]-1);

            // update internal world model according to the action that was just executed
            this.env.move(this.env.current_state, lastMove);
        }
        // update turn (above myTurn is still for the previous state)
        myTurn = !myTurn;

		if (myTurn) {            
            // For cut off depth
            int cut_off = 2;
            Move best_move = get_best_move(this.env.current_state, cut_off);
            
            return "(move " + (best_move.x1 + 1) + " " + (best_move.y1 + 1) + " " + (best_move.x2 + 1) + " " + (best_move.y2 + 1) + ")";
		}
        return "noop";    
	}


    /**
     * Helper function to compute the best move given current environment.
     * @return a Move object. The best move to take. May not be legal.
     */
	private Move get_best_move(State state, int start_cutoff_depth) {
        // Start timer
        long startTime = System.currentTimeMillis();

        // Check if there is a winning move
        Move move = check_if_terminal_move(state);
        if(move != null){
            return move; 
        };

        // Check if there are any defensive move
        move = check_defense_moves(state);
        if (move != null){
            return move;
        };

        Minimax minimax = new Minimax();
        Evaluation_Function evaluationFunction = currentState -> combined_evaluation(currentState);
        minimax.set_evaluation_function(evaluationFunction);
        Minimax.current_best_move = null;

        try {
            for (int i = start_cutoff_depth; i < 50; i++){
                State working_state = copy_state(state);
                minimax.minimax_alpha_beta(env, working_state, i - 1, 0, Minimax.NEGATIVE_INFINITY, Minimax.POSITIVE_INFINITY, myTurn, startTime);
                Minimax.current_best_move = Minimax.best_move;
            }
        }
        catch (Exception e){
            return Minimax.current_best_move;
        }
    
        return Minimax.current_best_move;
    }


    /**
     * To deep copy a state
     * @param state State to be deep copied
     * @return Copy of the state
     */
    public State copy_state(State state) {
        State new_state     = new State(this.env.width, this.env.height);
        char[][] board_copy = new char[this.env.height][this.env.width];

        for (int i = 0; i < this.env.height; i++)
            board_copy[i] = Arrays.copyOf(state.board[i], state.board[i].length);

        new_state.board      = board_copy;
        new_state.white_turn = state.white_turn;

        return new_state;
    }


    /**
     * Calculates the evaluation value based on the different evaluation functions
     * @param state State of the board
     * @return
     */
    private int combined_evaluation(State state) {
        ArrayList<Move> moves = env.get_legal_moves_in_all_positions(state);
        int evaluation = capture_potential_evaluation(state, moves) + protected_evaluation(state) - calculate_moves_to_goal(state) 
        + defense_line_evaluation(state, moves); 
        if (!state.white_turn){
            return -evaluation;
        }

        return evaluation;
    }


    // Check if there are any defensive moves
    /**
     * Check if there are any defensive moves in the current state
     * @param state State to be considered
     * @return A defensive move if one exist, else null
     */
    private Move check_defense_moves(State state){
        int defensive_height = state.white_turn ? 0 : env.height - 1; 
        char player = state.white_turn ? Environment.WHITE : Environment.BLACK; 

        // Go through width wise and check each defensive position
        for(int i=0; i < env.width; i++){
            if (state.board[defensive_height][i] == player){
                    ArrayList<Move> legal_moves = env.get_legal_moves_from_position(state, i, defensive_height);
                    for (Move move : legal_moves){
                        if (move.is_diagonal()){
                            return move;
                        }
                    }
                }
            }
        return null;
        }


    // Check if there is a winning move
    /**
     * Evaluates function: Check if there are any moves that will get to the winning move
     * @param state The state to be evaluated
     * @return An evaluation value that should be >>> than the other evaluation functions
     */
    private Move check_if_terminal_move(State state){
        int winning_height = state.white_turn ? env.height - 1 : 0; 

        for (int h = 0; h < env.height; h++) {
            for (int w = 0; w < env.width; w++){
                // Check if any legal; moves lead to winning move
                if (state.board[h][w] == (state.white_turn ? Environment.WHITE : Environment.BLACK)){
                        ArrayList<Move> legal_moves = env.get_legal_moves_in_all_positions(state);
                        for (Move move: legal_moves){
                            if (move.y2 == winning_height){
                                // return Integer.MAX_VALUE;
                                return move;
                            }
                        }
                    }
                }
            }

        return null;
    }



    /**
     * At the current state, how many possible captures are possible after the other player has made a turn to move
     * Can be L-shaped or diagonal (more priority <diagonal> since it is usually better to capture)
     * Diagonal moves are only possible if the opponent piece is diagonally infront of a friendly piece
     * Count the number of possible elimination after an opponent makes a move.
     * @param state State to be evaluated
     * @param moves Legal moves for the current state
     * @return Number of potential moves that an enemy can make that would result in its possible capture in the next turn.
     */
    private int capture_potential_evaluation(State state, ArrayList<Move> moves) {
        // Flip the player because we are testing the potential next state (if white moves then BLACK turn)
        char player = state.white_turn ? Environment.BLACK : Environment.WHITE;
        int one_step = state.white_turn ? -1 : 1; 
        int score = 0;

        /* Count number of possible elimination
        Get possible positions of the enemy (Get legal moves if the current state is the opponent's turn) */
        for (Move move : moves){            
            // Check if the new position is beside an opponent knight if so add 1.
            if (move.x2 < env.width - 1 && move.y2 + one_step < env.height && move.y2 + one_step >= 0){
                if(state.board[move.y2 + one_step][move.x2 + 1] == player){
                    score += 1;
                    continue;
                }
            }
            if (move.x2 > 0 && move.y2 + one_step < env.height && move.y2 + one_step >= 0){
                if (state.board[move.y2 + one_step][move.x2 - 1] == player){
                    score += 1;
                    continue;
                }
            }
        }
        
        return score;
    }


    /** 
     * At the current state, calculate the amount of friendly pieces that can protect other friendly pieces
     * The output `score` indicates the number of friendly pieces covered by other friendly pieces. 
     * @param state State to be evaluated
     * @return Number of possible states that allows for a friendly piece to cover another
     */
    private int protected_evaluation(State state) {
        int score = 0;

        // After the move, the state should be flipped (if white moves then the next state should be black's turn)
        char player = state.white_turn ? Environment.BLACK : Environment.WHITE;
        // Take white's action if BLACK turn
        int one_step = (player == Environment.BLACK) ? 1 : -1; 

        // Find friendly pieces, then check if its forward diagonals is another friendly piece
        for (int h = 0; h < env.height; h++){
            for (int w = 0; w < env.width; w++){
                if (state.board[w][h] == player){
                    // Check diagonal right (Check if it will go out of bounds)
                    if ((w + 1 < env.width) && (h + one_step < env.height) && (h + one_step >= 0) &&
                        (state.board[h + one_step][w + 1] == player)){
                        score += 1;
                        continue;
                    }
                    // Check diagonal left
                    if ((w > 0) && (h + one_step < env.height) && (h + one_step >= 0) &&
                    (state.board[h + one_step][w - 1] == player)){
                        score += 1;
                        continue;
                    }
                }
            }
        }

        return score;
    }


    /**
     * At the current state, how many possible moves can I make to get to the goal node (enemy baseline),
     * assuming that the path is clear.
     * @param state State to be evaluated
     * @return Total number of possible moves to get to destination (enemy baseline)
     */
    private int calculate_moves_to_goal(State state) {
        int score = 0;

        // Get player indicator (WHITE or BLACK)
        char player = state.white_turn ? Environment.WHITE : Environment.BLACK; 
        
        for (int h = 0; h < env.height; h++) {
            for (int w = 0; w < env.width; w++){
                // Check the total number of actions to get to destination
                if (state.board[h][w] == player){
                    // Check how many moves to get to enemy baseline if board was empty
                    // Assume every move, moves the player by 2 vertically
                    switch (player){
                        case Environment.WHITE: score += ((env.height - h) + 1) / 2; break;
                        case Environment.BLACK: score += (h + 2) / 2; break;
                    }
                }
            }
        }

        return score;
    }


    /**
     * This evaluation prioritises the formation of a defensive line in the friendly base as well as
     * any capture of enemy close one tile away from the friendly base.
     * @param state State to be evaluated 
     * @param moves Legal moves for the current state
     * @return Evaluation score to prioritise defense moves
     */
    private int defense_line_evaluation(State state, ArrayList<Move> moves){
        char player = state.white_turn ? Environment.WHITE : Environment.BLACK; 
        char opponent = state.white_turn ? Environment.BLACK : Environment.WHITE;
        for (Move move : moves){
            // Keep defensive line, enemy winning row
            // Attack any diagonal enemy infront of defensive line.
            switch(player) {
                case Environment.WHITE:
                    if (move.y1 == 0){
                        if (move.is_diagonal() && env.can_diagonal_move_capture(move, state, opponent)){
                            return Integer.MAX_VALUE - 1000;
                        }
                        return -(env.width << 5);
                    };
                    break;
                case Environment.BLACK: 
                    if (move.y1 == env.height - 1){
                        if (move.is_diagonal() && env.can_diagonal_move_capture(move, state, opponent)){
                            return Integer.MAX_VALUE - 1000;
                        }
                        return -(env.width << 5);
                    };
                    break;
            }
        }
        return 0;
    }


    // is called when the game is over or the match is aborted
	@Override
	public void cleanup() {
        // Reset all variables that need to be reset to restart the game
        this.env = null;
	}
}


