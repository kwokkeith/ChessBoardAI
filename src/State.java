public class State {
    public char[][] board;
    public boolean white_turn;
    static final char BLACK = 'B';
    static final char WHITE = 'W';
    static final char EMPTY = ' ';
    private final int width;

    // Constructor for class State
    public State(int width, int height){
        this.board = new char[height][width];

        // If White starts first
        this.white_turn = true;
        this.width = width;

        // Initialize the board with White and Black knights
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++){
                if (i < 2){
                    this.board[i][j] = WHITE;
                }
                else if (i > height - 3){
                    this.board[i][j] = BLACK;
                }
                else {
                    this.board[i][j] = EMPTY;
                }
            }
        }
    }

    public String toString() {
        int dash_count = this.width * 5 - 6;
        String line = "\n" + "-".repeat(dash_count) + "\n";
        String result = line;

        for (int i = 0; i < this.width; i++) {
            result += new String(this.board[i]).replaceAll("", " | ");
            result += line;
        }

        return result;
    }
}
