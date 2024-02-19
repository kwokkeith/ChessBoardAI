public class Move {
    public int x1, y1, x2, y2;
    
    /**
     * From position (x1, y1) to position (x2, y2). Direction: bottom-top, left-right
     * @param x1 From x-coordinate
     * @param y1 From y-coordinate
     * @param x2 To x-coordinate
     * @param y2 To y-coordinate
     */
    public Move(int x1, int y1, int x2, int y2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }


    /**
     * Checks if this move itself is diagonal
     * @return boolean
     */
    public boolean is_diagonal() {
        int changeInX = Math.abs(x2 - x1);
        int changeInY = Math.abs(y2 - y1);
        if (changeInX == 1 && changeInY == 1) {
            return true;
        }
        return false;
    }


    public String toString(){
        return "Move [X,Y] from [" + this.x1 + "," + this.y1 + "] to [" + this.x2 + "," + this.y2 + "]";
    }

}
