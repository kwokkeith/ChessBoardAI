public class Minimax {

    private static final int POSITIVE_INFINITY = Integer.MAX_VALUE;
    private static final int NEGATIVE_INFINITY = Integer.MIN_VALUE;
    private Evaluation_Function evaluation_function = null;
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
    private int minimax_alpha_beta(Environment current_env, int cutoff_depth, 
        int depth, int alpha, int beta, boolean maximising_player) {
        if (depth == cutoff_depth || current_env.current_state.isTerminal()) {
            // Must store the state
            return evaluation_function.evaluate(current_env.current_state); // Keith to compute
        }

        if (maximising_player) {
            int maxEvaluation = NEGATIVE_INFINITY;
            for (Move child_move : current_env.get_legal_moves_in_all_positions(current_env.current_state)) { // To implement somehow a way to get child states
                current_env.move(current_env.current_state, child_move);
                int evaluation = minimax_alpha_beta(current_env, cutoff_depth, depth + 1, alpha, beta, false);
                maxEvaluation = Math.max(maxEvaluation, evaluation);


                alpha = Math.max(alpha, evaluation);
                // Update best move (last move)


                if (depth != 0) {
                    current_env.undo_move(current_env.current_state, child_move); // Set the board to parent's board state.
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
            int minEvaluation = POSITIVE_INFINITY;
            for (Move child_move : current_env.get_legal_moves_in_all_positions(current_env.current_state)) { // To implement somehow a way to get child states
                current_env.move(current_env.current_state, child_move);
                
                int evaluation = minimax_alpha_beta(current_env,cutoff_depth, depth + 1, alpha, beta, true);
                minEvaluation = Math.min(minEvaluation, evaluation);
                beta = Math.min(beta, evaluation);


                if (depth != 0) {
                    current_env.undo_move(current_env.current_state, child_move); // Set the board to parent's board state.
                } else { // Root node: update best move.
                    if (minEvaluation == evaluation) {
                        best_move = child_move;
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


    public int run(Environment current_environment, int cutoff_depth, boolean maximising_player) throws Exception{
        
        if (evaluation_function == null) {
            throw new Exception("Evaluation method is unset in Minimax algorithm!");
        }
        
        return minimax_alpha_beta(current_environment, cutoff_depth, 0, NEGATIVE_INFINITY, POSITIVE_INFINITY, maximising_player);
    }

}


