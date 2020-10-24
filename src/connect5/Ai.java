package connect5;


import java.util.ArrayList;
import java.util.Random;


public class Ai {
	
		
	
	
	/**function minimax(node, depth, maximizingPlayer) is
    if depth = 0 or node is a terminal node then
        return the heuristic value of node
    if maximizingPlayer then
        value := −∞
        for each child of node do
            value := max(value, minimax(child, depth − 1, FALSE))
        return value
    else (* minimizing player *)
        value := +∞
        for each child of node do
            value := min(value, minimax(child, depth − 1, TRUE))
        return value
	
	*/
	
	// 
		private int maxDepth;
		
	    // Variable that holds which letter this player controls
		private int aiL;

		public Ai() {
			maxDepth = 4;
			aiL = Constants.O;
		}
		
		public Ai(int maxDepth, int aiLetter) {
			this.maxDepth = maxDepth;
			this.aiL = aiLetter;
		}
		
		public int getMaxDepth() {
			return maxDepth;
		}

		public void setMaxDepth(int maxDepth) {
			this.maxDepth = maxDepth;
		}

		public int getAiLetter() {
			return aiL;
		}

		public void setAiLetter(int aiLetter) {
			this.aiL = aiLetter;
		}

	    //Initiates the MiniMax algorithm
		public Move miniMax(Board board) {
	        //If the X plays then it wants to MAXimize the heuristics value
	        if (aiL == Constants.X) {
	            return max(new Board(board), 0);
	        }
	        //If the O plays then it wants to MINimize the heuristics value
	        else {
	            return min(new Board(board), 0);
	        }
		}

		
		
		
	    // The max and min functions are called interchangingly, one after another until a max depth is reached
		public Move max(Board board, int depth) {
	        Random r = new Random();

	        /* If MAX is called on a state that is terminal or after a maximum depth is reached,
	         * then a heuristic is calculated on the state and the move returned.
	         */
			if((board.checkGameOver()) || (depth == maxDepth)) {
				Move lastMove = new Move(board.getLastMove().getRow(), board.getLastMove().getCol(), board.evaluate());
				return lastMove;
			}
	        //The children-moves of the state are calculated
			ArrayList<Board> children = new ArrayList<Board>(board.getChildren(Constants.X));
			Move maxMove = new Move(Integer.MIN_VALUE);
			for (Board child : children) {
	            //And for each child min is called, on a lower depth
				Move move = min(child, depth + 1);
	            //The child-move with the greatest value is selected and returned by max
				if(move.getValue() >= maxMove.getValue()) {
	                if ((move.getValue() == maxMove.getValue())) {
	                    //If the heuristic has the same value then we randomly choose one of the two moves
	                    if (r.nextInt(2) == 0) {
	                        maxMove.setRow(child.getLastMove().getRow());
	                        maxMove.setCol(child.getLastMove().getCol());
	                        maxMove.setValue(move.getValue());
	                    }
	                }
	                else {
	                    maxMove.setRow(child.getLastMove().getRow());
	                    maxMove.setCol(child.getLastMove().getCol());
	                    maxMove.setValue(move.getValue());
	                }
				}
			}
			return maxMove;
		}

	    //Min works similarly to max
		public Move min(Board board, int depth) {
	        Random r = new Random();

			if((board.checkGameOver()) || (depth == maxDepth)) {
				Move lastMove = new Move(board.getLastMove().getRow(), board.getLastMove().getCol(), board.evaluate());
				return lastMove;
			}
			ArrayList<Board> children = new ArrayList<Board>(board.getChildren(Constants.O));
			Move minMove = new Move(Integer.MAX_VALUE);
			for (Board child : children) {
				Move move = max(child, depth + 1);
				if(move.getValue() <= minMove.getValue()) {
	                if ((move.getValue() == minMove.getValue())) {
	                    if (r.nextInt(2) == 0) {
	                        minMove.setRow(child.getLastMove().getRow());
	                        minMove.setCol(child.getLastMove().getCol());
	                        minMove.setValue(move.getValue());
	                    }
	                }
	                else {
	                        minMove.setRow(child.getLastMove().getRow());
	                        minMove.setCol(child.getLastMove().getCol());
	                        minMove.setValue(move.getValue());
	                }
	            }
	        }
	        return minMove;
		}
		
		
		
		
}
