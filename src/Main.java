public class Main {
	
	/**
	 * starts the game player and waits for messages from the game master <br>
	 * Command line options: [port]
	 */
	public static void main(String[] args){
		try{
			// TEST 1: Move a piece
			// env.move(env.current_state, new Move(0, 0, 1, 2));
			// System.out.println("Test 1: env.current_state:" + env.current_state);

			// // TEST 2: Check the next legal moves
			// ArrayList<Move> test2moves = env.get_legal_moves_in_all_positions(env.current_state);
			// System.out.println("Test 2: env.current_state:" + env.current_state);
			// System.out.println("Printing out all moves:");
			// for (Move move: test2moves){
			// 	System.out.println(move);
			// }

			// // TEST 3: Random move up to `i` turn
			// for (int i = 0; i < 10; i ++){
			// 	// *** Uncomment code below to check legal moves
			// 	// for (Move move : moves) {
			// 	// 	System.out.println(move);
			// 	// }
			// 	ArrayList<Move> test3moves = env.get_legal_moves_in_all_positions(env.current_state);
			// 	Random rand = new Random();
			// 	int j = rand.nextInt(test3moves.size());
			// 	env.move(env.current_state, test3moves.get(j));
			// 	System.out.println(env.current_state);
			// }

			Agent agent = new MyAgent();

			int port=4001;
			if(args.length>=1){
				port=Integer.parseInt(args[0]);
			}
			GamePlayer gp=new GamePlayer(port, agent);
			gp.waitForExit();
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(-1);
		}
	}
}
