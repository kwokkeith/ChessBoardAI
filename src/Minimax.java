import java.util.ArrayList;

public class Minimax {

    private static final int POSITIVE_INFINITY = Integer.MAX_VALUE;
    private static final int NEGATIVE_INFINITY = Integer.MIN_VALUE;
    public Evaluation_Function evaluation_function = null;
    public static Move best_move = null;


    /**
     * Returns best score for either maximising or minimising player given input currentState.
     * @param current_env root environment. To begin "simulation" of scoring.
     * @param cutoff_depth controls the cutoff of the recursion. If 10, means <= 10 recursive calls before end
     * @param alpha highest score of a State so far  for maximiser. Only can be set during maximiser's turn
     * @param beta highest score of a State so far for minimiser. Only can be set during minimiser's turn
     * @param maximising_player boolean to determine whose turn it is
     * @return root state's highest possible score. (Input state's highest possible score)
     */
    private int minimax_alpha_beta(Environment current_env, State state, int cutoff_depth, 
        int depth, int alpha, int beta, boolean maximising_player) throws Exception {

        // System.out.println("Minimax alpha beta search begins...");

        if (depth == cutoff_depth ) {
            // Must store the state
            return evaluation_function.evaluate(state); // Keith to compute
        }


        if (maximising_player) {
            // System.out.println("\nMaximising player turn:");

            int maxEvaluation = NEGATIVE_INFINITY;

            // If root node then pick a random action first
            ArrayList<Move> moves = current_env.get_legal_moves_in_all_positions(state);
            if (depth == 0){
                best_move = moves.get(0);
            }

            for (Move child_move : moves) { // To implement somehow a way to get child states
                current_env.move(state, child_move);
                int evaluation = minimax_alpha_beta(current_env, state, cutoff_depth, depth + 1, alpha, beta, false);
                maxEvaluation = Math.max(maxEvaluation, evaluation);


                alpha = Math.max(alpha, evaluation);
                // Update best move (last move)

                if (depth != 0) {
                    current_env.undo_move(state, child_move); // Set the board to parent's board state.
                } else { // Root node: update best move.
                    if (maxEvaluation == evaluation) {
                        best_move = child_move;
                    }
                }
                if (beta <= alpha) {
                    break;
                }
            }

            return maxEvaluation;

        } else {
            // System.out.println("\nMinimising player turn:");

            int minEvaluation = POSITIVE_INFINITY;
            ArrayList<Move> moves = current_env.get_legal_moves_in_all_positions(state);
            
            // If root node then pick a random action first
            if (depth == 0){
                best_move = moves.get(0);
            }

            for (Move child_move : current_env.get_legal_moves_in_all_positions(state)) { // To implement somehow a way to get child states
                current_env.move(state, child_move);
                
                int evaluation = minimax_alpha_beta(current_env, state, cutoff_depth, depth + 1, alpha, beta, true);
                minEvaluation = Math.min(minEvaluation, evaluation);
                beta = Math.min(beta, evaluation);

                if (depth != 0) {
                    current_env.undo_move(state, child_move); // Set the board to parent's board state.
                } else { // Root node: update best move.
                    if (minEvaluation == evaluation) {
                        best_move = child_move;
                        System.out.println("Best move: " + best_move.toString());
                    }
                }
                if (beta <= alpha) {
                    break;
                }
            }
            return minEvaluation;

        }
    }

    public void set_evaluation_function(Evaluation_Function evaluation_function) {
        this.evaluation_function = evaluation_function;
    }


    public Move run(Environment current_environment, State state, int cutoff_depth, boolean maximising_player) {
        
        try {
            for (int i = 1; i <= cutoff_depth; i++) {
                minimax_alpha_beta(current_environment, state, i, 0, NEGATIVE_INFINITY, POSITIVE_INFINITY, maximising_player);
            }
        } catch(Exception e) {
            return best_move;
        }
        return best_move;
    }

}


