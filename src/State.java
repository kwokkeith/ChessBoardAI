public class State {
    public char[][] board;
    public boolean white_turn;
    static final char BLACK = 'B';
    static final char WHITE = 'W';
    static final char EMPTY = ' ';
    private final int width;
    private final int height;

    // Constructor for class State
    public State(int width, int height){
        this.board = new char[width][height];

        // If White starts first
        this.white_turn = true;
        this.width = width;
        this.height = height;

        // Initialize the board with White and Black knights
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++){
                if (i < 2){
                    this.board[j][i] = BLACK;
                }
                else if (i > height - 3){
                    this.board[j][i] = WHITE;
                }
                else {
                    this.board[j][i] = EMPTY;
                }
            }
        }
    }

    public boolean isTerminal() {
        for (int j = 0; j < width; j++){
            if (this.board[j][0] == BLACK || this.board[j][height-1] == WHITE ) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
    
        // Append the top border of the board
        sb.append("+");
        for (int i = 0; i < width * 3 - 1; i++) {
            sb.append("-");
        }
        sb.append("+\n");
    
        // Append each row of the board
        for (int i = 0; i < height; i++) {
            sb.append("| ");
            for (int j = 0; j < width; j++) {
                sb.append(board[j][i]).append(" | ");
            }
            sb.append("\n+");
            for (int k = 0; k < width; k++) {
                sb.append("---+");
            }
            sb.append("\n");
        }
    
        return sb.toString();
    }
    

}
