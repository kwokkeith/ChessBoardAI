public class Minimax {

    private static final int POSITIVE_INFINITY = Integer.MAX_VALUE;
    private static final int NEGATIVE_INFINITY = Integer.MIN_VALUE;
    private Evaluation_Function evaluation_function = null;

    /**
     * Returns best score for either maximising or minimising player given input currentState.
     * @param current_state root state. To begin "simulation" of scoring.
     * @param depth controls the cutoff of the recursion. If 10, means <= 10 recursive calls before end
     * @param alpha highest score of a State so far  for maximiser. Only can be set during maximiser's turn
     * @param beta highest score of a State so far for minimiser. Only can be set during minimiser's turn
     * @param maximising_player boolean to determine whose turn it is
     * @return root state's highest possible score. (Input state's highest possible score)
     */
    private int minimax_alpha_beta(State current_state, int depth, int alpha, int beta, boolean maximising_player) {
        if (depth == 0 || current_state.isTerminal()) {
            // Must store the state
            return evaluation_function.evaluate(current_state); // Keith to compute
        }

        if (maximising_player) {
            int maxEvaluation = NEGATIVE_INFINITY;
            for (State childState : current_state.getChildStates()) { // To implement somehow a way to get child states
                int evaluation = minimax_alpha_beta(childState, depth - 1, alpha, beta, false);
                maxEvaluation = Math.max(maxEvaluation, evaluation);
                alpha = Math.max(alpha, evaluation);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEvaluation;

        } else {
            int minEvaluation = POSITIVE_INFINITY;
            for (State childState : current_state.getChildStates()) { // To implement somehow a way to get child states
                int evaluation = minimax_alpha_beta(childState, depth - 1, alpha, beta, true);
                minEvaluation = Math.min(minEvaluation, evaluation);
                beta = Math.min(beta, evaluation);
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


    public int run(State current_state, int depth, boolean maximising_player) throws Exception{
        if (evaluation_function == null) {
            throw new Exception("Evaluation method is unset in Minimax algorithm!");
        }
        
        return minimax_alpha_beta(current_state, depth, NEGATIVE_INFINITY, POSITIVE_INFINITY, maximising_player);
    }

}


