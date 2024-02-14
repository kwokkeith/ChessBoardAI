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

	// lastMove is null the first time nextAction gets called (in the initial state)
    // otherwise it contains the coordinates x1,y1,x2,y2 of the move that the last player did
    public String nextAction(int[] lastMove) {
        if (lastMove != null) {
            int x1 = lastMove[0], y1 = lastMove[1], x2 = lastMove[2], y2 = lastMove[3];
            String roleOfLastPlayer;
            if (myTurn && role.equals("white") || !myTurn && role.equals("black")) {
                roleOfLastPlayer = "white";
            } else {
                roleOfLastPlayer = "black";
            }
            System.out.println(roleOfLastPlayer + " moved from " + x1 + "," + y1 + " to " + x2 + "," + y2);

            // TODO: 1. update your internal world model according to the action that was just executed
            this.env.move(this.env.current_state, new Move(x1 - 1, y1 - 1, x2 - 1, y2 - 1));
        }
		
    	// update turn (above that line it myTurn is still for the previous state)
		myTurn = !myTurn;
		if (myTurn) {
			// TODO: 2. run alpha-beta search to determine the best move
            Move best_move = get_best_move();
            
            // Check if best move is a legal move
            // TODO: Implement some legal checking in the event our algorithm is wrong
            


			return "(move " + (best_move.x1 + 1) + " " + (best_move.y1 + 1) + " " + (best_move.x2 + 1) + " " + (best_move.y2 + 1) + ")";
		} else {
			return "noop";
		}
	}

	private Move get_best_move() {
        // TODO: Return the best move to send to game player
        
    }

    // is called when the game is over or the match is aborted
	@Override
	public void cleanup() {
		// TODO: cleanup so that the agent is ready for the next match
        // Reset all variables that need to be reset to restart the game
        this.env = null;
	}
}
