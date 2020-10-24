package connect5;


import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import gui.Graphics;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;


public class Board {

	// Immediate move that led to this board.
	private Move lastMove;

	// A variable to store the symbol of the player who played last,
	// leading to the current board state.
	private int lastSymbolPlayed;

	private int winner;
	private int [][] gameBoard;

	private boolean overflowOccured;

	private boolean gameOver;

	private  int turn;


	// constructor
	public Board() {
		this.lastMove = new Move();
		this.lastSymbolPlayed = Constants.O;
		this.winner = Constants.EMPTY;
		this.gameBoard = new int[7][8];
		this.overflowOccured = false;
		this.gameOver = false;
		turn = 0;
		for(int i=0; i<7; i++) {
			for(int j=0; j<8; j++) {
				gameBoard[i][j] = Constants.EMPTY;
			}
		}
	}


	// copy constructor
	public Board(Board board) {
		lastMove = board.lastMove;
		lastSymbolPlayed = board.lastSymbolPlayed;
		winner = board.winner;
		gameBoard = new int[7][8];
		this.overflowOccured = false;
		this.gameOver = false;
		turn = 0;
		for(int i=0; i<7; i++) {
			for(int j=0; j<8; j++) {
				gameBoard[i][j] = board.gameBoard[i][j];
			}
		}
	}


	public Move getLastMove() {
		return lastMove;
	}

	public void setLastMove(Move lastMove) {
		this.lastMove.setRow(lastMove.getRow());
		this.lastMove.setCol(lastMove.getCol());
		this.lastMove.setValue(lastMove.getValue());
	}


	public int getLastSymbolPlayed() {
		return lastSymbolPlayed;
	}


	public void setLastSymbolPlayed(int lastLetterPlayed) {
		this.lastSymbolPlayed = lastLetterPlayed;
	}


	public int[][] getGameBoard() {
		return gameBoard;
	}


	public void setGameBoard(int[][] gameBoard) {
		for(int i=0; i<7; i++) {
			for(int j=0; j<8; j++) {
				this.gameBoard[i][j] = gameBoard[i][j];
			}
		}
	}


	public int getWinner() {
		return winner;
	}


	public void setWinner(int winner) {
		this.winner = winner;
	}


	public int getTurn() {
		return turn;
	}


	public void setTurn(int turn) {
		turn = turn;
	}


	public boolean isGameOver() {
		return gameOver;
	}


	public void setGameOver(boolean isGameOver) {
		this.gameOver = isGameOver;
	}


	public boolean hasOverflowOccured() {
		return overflowOccured;
	}


	public void setOverflowOccured(boolean overflowOccured) {
		this.overflowOccured = overflowOccured;
	}


	// Makes a move based on the given column.
	// It finds automatically in which row the checker should be inserted.
	public void makeMove(int col, int letter) {
		try {
			// The variable "lastMove" must be changed before the variable
			// "gameBoard[][]" because of the function "getRowPosition(col)".
			this.lastMove = new Move(getEmptyRowPosition(col), col);
			this.lastSymbolPlayed = letter;
			this.gameBoard[getEmptyRowPosition(col)][col] = letter;
			turn++;
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Column " + (col+1) + " run out of space");
			setOverflowOccured(true);
		}
	}


	// Makes the specified cell in the border empty.
	public void undoMove(int row, int col, int letter) {
		this.gameBoard[row][col] = 0;
		if (letter == Constants.O) {
			this.lastSymbolPlayed = Constants.X;
		} else if (letter == Constants.X) {
			this.lastSymbolPlayed = Constants.O;
		}
		turn--;
	}


	// This function is used when we want to search the whole board,
	// without getting out of borders.
	public boolean canMove(int row, int col) {
		if ((row <= -1) || (col <= -1) || (row > 6) || (col > 7)) {
			return false;
		}
		return true;
	}


	public boolean checkFullColumn(int col) {
		if (gameBoard[0][col] == Constants.EMPTY)
			return false;
		return true;
	}


	// It returns the position of the first empty row in a column.
	public int getEmptyRowPosition(int col) {
		int rowPosition = -1;
		for (int row=0; row<7; row++) {
			if (gameBoard[row][col] == Constants.EMPTY) {
				rowPosition = row;
			}
		}
		return rowPosition;
	}


	/* Generates the children of the state.
	 * max possible children is 8
	 */
	public ArrayList<Board> getChildren(int letter) {
		ArrayList<Board> children = new ArrayList<Board>();
		for(int col=0; col<8; col++) {
			if(!checkFullColumn(col)) {
				Board child = new Board(this);
				child.makeMove(col, letter);
				children.add(child);
			}
		}
		return children;
	}


	public int evaluate() {
		int Xlines = 0;
		int Olines = 0;

		if (checkWinState()) {
			if(getWinner() == Constants.X) {
				Xlines = Xlines + 100;
			} else if (getWinner() == Constants.O) {
				Olines = Olines + 100;
			}
		}

		Xlines  = Xlines + count3InARow(Constants.X) * 10 + count2InARow(Constants.X);
		Olines  = Olines + count3InARow(Constants.O) * 10 + count2InARow(Constants.O);

		// if the result is 0, then it'a a draw 
		return Xlines - Olines;
	}


	/*
	 * Terminal win check.
	 * It checks whether somebody has won the game.
	 */
	public boolean checkWinState() {

		// Check for 4 consecutive checkers in a row, horizontally.
		for (int i=0; i<7; i++) {
			for (int j=0; j<8; j++) {
				if (canMove(i, j+4)) {
					if (gameBoard[i][j] == gameBoard[i][j+1]
							&& gameBoard[i][j] == gameBoard[i][j+2]
									&& gameBoard[i][j] == gameBoard[i][j+3]
											&& gameBoard[i][j] == gameBoard[i][j+4]
													&& gameBoard[i][j] != Constants.EMPTY) {
						setWinner(gameBoard[i][j]);
						return true;
					}
				}
			}
		}

		// Check for 4 consecutive checkers in a row, vertically.
		for (int i=0; i<7; i++) {
			for (int j=0; j<8; j++) {
				if (canMove(i-4, j)) {
					if (gameBoard[i][j] == gameBoard[i-1][j]
							&& gameBoard[i][j] == gameBoard[i-2][j]
									&& gameBoard[i][j] == gameBoard[i-3][j]
											&& gameBoard[i][j] == gameBoard[i-4][j]
													&& gameBoard[i][j] != Constants.EMPTY) {
						setWinner(gameBoard[i][j]);
						return true;
					}
				}
			}
		}


		// Check for 4 consecutive checkers in a row, in descending diagonals.
		for (int i=0; i<7; i++) {
			for (int j=0; j<8; j++) {
				if (canMove(i+4, j+4)) {
					if (gameBoard[i][j] == gameBoard[i+1][j+1]
							&& gameBoard[i][j] == gameBoard[i+2][j+2]
									&& gameBoard[i][j] == gameBoard[i+3][j+3]
											&& gameBoard[i][j] == gameBoard[i+4][j+4]
													&& gameBoard[i][j] != Constants.EMPTY) {
						setWinner(gameBoard[i][j]);
						return true;
					}
				}
			}
		}


		// Check for 4 consecutive checkers in a row, in ascending diagonals.
		for (int i=0; i<7; i++) {
			for (int j=0; j<8; j++) {
				if (canMove(i-4, j+4)) {
					if (gameBoard[i][j] == gameBoard[i-1][j+1]
							&& gameBoard[i][j] == gameBoard[i-2][j+2]
									&& gameBoard[i][j] == gameBoard[i-3][j+3] 
											&& gameBoard[i][j] == gameBoard[i-4][j+4]	
													&& gameBoard[i][j] != Constants.EMPTY) {
						setWinner(gameBoard[i][j]);
						return true;
					}
				}
			}
		}

		setWinner(Constants.EMPTY);  // set nobody as the winner
		return false;
	}


	public boolean checkGameOver() {
		// Check if there is a winner.
		if (checkWinState()) {
			return true;
		}

		// Check for an empty cell, i.e. check to find if it is a draw.
		// The game is in draw state, if all cells are full
		// and nobody has won the game.
		for(int row=0; row<7; row++) {
			for(int col=0; col<8; col++) {
				if(gameBoard[row][col] == Constants.EMPTY) {
					return false;
				}
			}
		}

		return true;
	}


	// It returns the frequency of 3 checkers in a row,
	// for the given player.
	public int count3InARow(int playerSymbol) {

		int times = 0;


		// Check for 3 consecutive checkers in a row, horizontally.
		for (int i=0; i<7; i++) {
			for (int j=0; j<8; j++) {
				if (canMove(i, j+2)) {
					if (gameBoard[i][j] == gameBoard[i][j+1]
							&& gameBoard[i][j] == gameBoard[i][j+2]
									&& gameBoard[i][j] == playerSymbol) {
						times++;
					}
				}
			}
		}


		// Check for 3 consecutive checkers in a row, vertically.
		for (int i=0; i<7; i++) {
			for (int j=0; j<8; j++) {
				if (canMove(i-2, j)) {
					if (gameBoard[i][j] == gameBoard[i-1][j]
							&& gameBoard[i][j] == gameBoard[i-2][j]
									&& gameBoard[i][j] == playerSymbol) {
						times++;
					}
				}
			}
		}


		// Check for 3 consecutive checkers in a row, in descending diagonal.
		for (int i=0; i<7; i++) {
			for (int j=0; j<8; j++) {
				if (canMove(i+2, j+2)) {
					if (gameBoard[i][j] == gameBoard[i+1][j+1]
							&& gameBoard[i][j] == gameBoard[i+2][j+2]
									&& gameBoard[i][j] == playerSymbol) {
						times++;
					}
				}
			}
		}


		// Check for 3 consecutive checkers in a row, in ascending diagonal.
		for (int i=0; i<7; i++) {
			for (int j=0; j<8; j++) {
				if (canMove(i-2, j+2)) {
					if (gameBoard[i][j] == gameBoard[i-1][j+1]
							&& gameBoard[i][j] == gameBoard[i-2][j+2]
									&& gameBoard[i][j] == playerSymbol) {
						times++;
					}
				}
			}
		}

		return times;

	}


	// It returns the frequency of 2 checkers in a row,
	// for the given player.
	public int count2InARow(int player) {

		int times = 0;

		// Check for 2 consecutive checkers in a row, horizontally.
		for (int i=0; i<7; i++) {
			for (int j=0; j<8; j++) {
				if (canMove(i, j+1)) {
					if (gameBoard[i][j] == gameBoard[i][j+1]
							&& gameBoard[i][j] == player) {
						times++;
					}
				}
			}
		}


		// Check for 3 consecutive checkers in a row, vertically.
		for (int i=0; i<7; i++) {
			for (int j=0; j<8; j++) {
				if (canMove(i-1, j)) {
					if (gameBoard[i][j] == gameBoard[i-1][j]
							&& gameBoard[i][j] == player) {
						times++;
					}
				}
			}
		}


		// Check for 3 consecutive checkers in a row, in descending diagonal.
		for (int i=0; i<7; i++) {
			for (int j=0; j<8; j++) {
				if (canMove(i+1, j+1)) {
					if (gameBoard[i][j] == gameBoard[i+1][j+1]
							&& gameBoard[i][j] == player) {
						times++;
					}
				}
			}
		}


		// Check for 3 consecutive checkers in a row, in ascending diagonal.
		for (int i=0; i<7; i++) {
			for (int j=0; j<8; j++) {
				if (canMove(i-1, j + 1)) {
					if (gameBoard[i][j] == gameBoard[i-1][j+1]
							&& gameBoard[i][j] == player) {
						times++;
					}
				}
			}
		}

		return times;		
	}

	
	
	public static void playMusic(String filepath) throws IOException {
		InputStream music;

	
			music = new FileInputStream(new File(filepath));
			AudioStream audios = new AudioStream(music);
			AudioPlayer.player.start(audios);
		
		
	}
	
	
	/** public static void AnimatedGif() throws MalformedURLException{
	        URL url = new URL("\\Users\\leona\\Desktop\\c0soEte.gif");
	        Icon icon = new ImageIcon(url);
	        JLabel label = new JLabel(icon);

	        JFrame f = new JFrame("Animation");
	        f.getContentPane().add(label);
	        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        f.pack();
	        f.setLocationRelativeTo(null);
	        f.setVisible(true);
	    
		
	}
	
	/** public static void saveGameDataToFile(String string) {   

	    Component frame = null;
		try {   
	        FileOutputStream fileStream = new FileOutputStream(string);   
	        ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);   

	        objectStream.writeObject(Move.getRow());   
	       objectStream.writeObject(color);   
	        objectStream.writeObject(snake);   
	        objectStream.writeObject(food);   
	        objectStream.writeObject(new Integer(score));   

	        objectStream.writeObject(barrier);   
	        objectStream.writeObject(new Boolean(needToGenerateFood));   
	        objectStream.writeObject(new Boolean(needToGenerateBarrie));   

	        objectStream.close();   
	        fileStream.close();   

	        JOptionPane.showConfirmDialog(frame, 
	            "Save game state successfully.", 
	            "Snake Game",   
	            JOptionPane.DEFAULT_OPTION);   
	    } catch (Exception e) {   
	        JOptionPane.showConfirmDialog(frame, 
	            e.toString() + "\nFail to save game state.",   
	            "Snake Game", 
	            JOptionPane.DEFAULT_OPTION);   
	    }   
	}   
	    */
	

/**	public static void memory(int[][] gameBoard) throws IOException {
		FileWriter write = new FileWriter("\\Users\\leona\\Desktop\\Memory.txt");
		BufferedWriter bw = new BufferedWriter(write);
		
		bw.write("Turn:  " + Integer.toString(getTurn()));
		bw.newLine();
		bw.write("| 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 |");
		bw.newLine();
		for (int i=0; i<7; i++) {
			for (int j=0; j<8; j++) {
				if (j!=7) {
					if (gameBoard[i][j] == 1) {
						bw.write("| " + "X" + " ");
					} else if (gameBoard[i][j] == -1) {
						bw.write("| " + "O" + " ");
					} else {
						bw.write("| " + "-" + " ");
					}
				} else {
					if (gameBoard[i][j] == 1) {
						bw.write("| " + "X" + " |");
						bw.newLine();
					} else if (gameBoard[i][j] == -1) {
						bw.write("| " + "O" + " |");
						bw.newLine();
					} else {
						bw.write("| " + "-" + " |");
						bw.newLine();
					}
				}
			
			}
			
		}	
		bw.close();
	}
*/




	// It prints the board on the console.
	public static void printBoard(int[][] gameBoard) {

		System.out.println("  1   2   3   4   5   6   7   8  ");
		System.out.println();
		for (int i=0; i<7; i++) {
			for (int j=0; j<8; j++) {
				if (j!=7) {
					if (gameBoard[i][j] == 1) {
						System.out.print("| " + "P" + " ");
					} else if (gameBoard[i][j] == -1) {
						System.out.print("| " + "M" + " ");
					} else {
						System.out.print("| " + " " + " ");
					}
				} else {
					if (gameBoard[i][j] == 1) {
						System.out.println("| " + "P" + " |");
					} else if (gameBoard[i][j] == -1) {
						System.out.println("| " + "M" + " |");
					} else {
						System.out.println("| " + " " + " |");
					}
				}
			}
		}

	}


}
