import java.util.ArrayList;

public class MyAgent implements Agent {
    private String role; // the name of this agent's role (white or black)
	private int playclock; // this is how much time (in seconds) we have before nextAction needs to return a move
	private boolean myTurn; // whether it is this agent's turn or not
	private int width, height; // dimensions of the board
    private Environment env; // To know about board environment
	/*
		init(String role, int playclock) is called once before you have to select the first action. Use it to initialize the agent. role is either "white" or "black" and playclock is the number of seconds after which nextAction must return.
	*/
    public void init(String role, int width, int height, int playclock) {
		this.role = role;
		this.playclock = playclock;
		myTurn = !role.equals("white");
		this.width = width;
		this.height = height;

		// TODO: add your own initialization code here
        this.env = new Environment(width, height);

		
    }


    /**
     * Computes the next best course of action. Then executes it
     * @param lastMove Coordinates of the previous move in the form (x1, y1, x2, y2). null on game start.
     */
    public String nextAction(int[] lastMoveCoordinates) {

        Move lastMove = new Move(lastMoveCoordinates[0],lastMoveCoordinates[1], lastMoveCoordinates[2], lastMoveCoordinates[3]);

        if (lastMove != null) {
            String roleOfLastPlayer;
            if (myTurn && role.equals("white") || !myTurn && role.equals("black")) {
                roleOfLastPlayer = "white";
            } else {
                roleOfLastPlayer = "black";
            }
            System.out.println(roleOfLastPlayer + " moved from [X,Y] : [" + lastMove.x1 + "," + lastMove.y1 + "] to [" + lastMove.x2 + "," + lastMove.y2 + "]");

            // TODO: 1. update your internal world model according to the action that was just executed
            this.env.move(this.env.current_state, lastMove);
        }
		
    	// update turn (above that line it myTurn is still for the previous state)
		myTurn = !myTurn;

		if (myTurn) {

            // If i understand correctly; the root node of minimax a-b algo uses 
            // this.env.current_state here as a root node.

			// TODO: 2. run alpha-beta search to determine the best move
            
            Move best_move = new Move(1,1,1,1);
            //Move best_move = get_best_move();
            
            // Check if best move is a legal move
            // TODO: Implement some legal checking in the event our algorithm is wrong
            


			return "(move " + (best_move.x1 + 1) + " " + (best_move.y1 + 1) + " " + (best_move.x2 + 1) + " " + (best_move.y2 + 1) + ")";
		} else {
			return "noop";
		}
	}

    /**
     * Helper function to compute the best move given current environment.
     * @return a Move object. The best move to take. May not be legal.
     */
	private Move get_best_move(State state) {
        // TODO: Return the best move to send to game player
        Minimax minimaxAlgorithm = new Minimax();
        final int DEPTH_CUT_OFF = 10; // Hardcoded. Can change later. Controls how deep the recursion is
        //TODO: make minimaxalgorithm return a Move object
        Evaluation_Function evaluationFunction = currentState -> combined_evaluation(currentState);
        minimaxAlgorithm.set_evaluation_function(evaluationFunction);
        try {
            minimaxAlgorithm.run(env, DEPTH_CUT_OFF, myTurn);
        } catch(Exception e) {
            System.out.println("Exception caught");
        }
        return Minimax.best_move;
        
    }

    private int combined_evaluation(State state) {
        return 10 * capture_potential_evaluation(state) + 5 * protected_evaluation(state) - calculate_moves_to_goal(state);
    }

    // Implementation of alpha-beta pruning algorithm
    // Count number of possible elimination
    private int capture_potential_evaluation(State state) {
        /* `state`: The state to be evaluated
         * At the current state, how many possible captures are possible after the other player has made a turn to move
         * Can be L-shaped or diagonal (more priority <diagonal> since it is usually better to capture)
         * Diagonal moves are only possible if the opponent piece is diagonally infront of a friendly piece 
         * 
         * The output `score` indicates the number of potential moves that an enemy can 
         * make that would result in its possible capture in the next turn*/

        // Flip the player because we are testing the potential next state (if white moves then BLACK turn)
        char player = state.white_turn ? Environment.BLACK : Environment.WHITE;
        int one_step = state.white_turn ? 1 : -1; 
        int score = 0;
        int weight = 1;

        /* Count number of possible elimination
        Get possible positions of the enemy (Get legal moves if the current state is the opponent's turn) */

        // Find legal moves of opponent
        ArrayList<Move> moves = env.get_legal_moves_in_all_positions(state);
        for (Move move : moves){
            // Check if the new position is beside an opponent knight if so add 1.
            if (move.x2 < env.width - 1 && move.y2 + one_step < env.height && move.y2 + one_step >= 0 &&
            state.board[move.x2 + 1][move.y2 + one_step] == player){
                score += weight;
                continue;
            }
            if (move.x2 > 0 && move.y2 + one_step < env.height && move.y2 + one_step >= 0 &&
                state.board[move.x2 - 1][move.y2 + one_step] == player){
                score += weight;
                continue;
            }
        }

        return score;
    }

    private int protected_evaluation(State state) {
        /* `state`: The state to be evaluated
         * At the current state, calculate the amount of friendly pieces that can protect other friendly pieces
         * The output `score` indicates the number of friendly pieces covered by other friendly pieces.
         */
        int score = 0;
        int weight = 1;

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
                        (state.board[w + 1][h + one_step] == player)){
                        score += weight;
                        continue;
                    }
                    // Check diagonal left
                    if ((w > 0) && (h + one_step < env.height) && (h + one_step >= 0) &&
                    (state.board[w - 1][h + one_step] == player)){
                        score += weight;
                        continue;
                    }
                }
            }
        }

        return score;
    }

    private int calculate_moves_to_goal(State state) {
        /* `state`: The state to be evaluated
         * At the current state, how many possible moves can I make to get to the goal node (enemy baseline)
         * Assume that the path is clear
         * 
         * The output `score` indicates the total number of possible moves to get to destination */

        int score = 0;

        // Get player indicator (WHITE or BLACK)
        char player = state.white_turn ? Environment.BLACK : Environment.WHITE; 
        int two_step = (player == Environment.WHITE) ? 2 : -2; 

        for (int h = 0; h < env.height; h++) {
            for (int w = 0; w < env.width; w++){
                // Check the total number of actions to get to destination
                if (state.board[w][h] == player){
                    // Check how many moves to get to enemy baseline if board was empty
                    // Assume every move, moves the player by 2 vertically
                    switch (player){
                        case Environment.WHITE: score += ((env.height - h) + 1) / 2;
                        case Environment.BLACK: score += (h + 2) / 2;
                    }

                }
            }
        }

        return score;
    }

    // is called when the game is over or the match is aborted
	@Override
	public void cleanup() {
		// TODO: cleanup so that the agent is ready for the next match
        // Reset all variables that need to be reset to restart the game
        this.env = null;
	}
}

// Endgame Scenarios: In later stages of the game, the evaluation function could shift to prioritize reaching the goal line over other considerations,
// especially when few pieces are left on the board.

