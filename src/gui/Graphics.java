package gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import connect5.Board;
import connect5.Constants;
import connect5.Ai;
import connect5.Move;
import connect5.Parameters;


public class Graphics {

	static Board board;
	static JFrame frameMainWindow;
	static JFrame frameGameOver;

	static JPanel panelMain;
	static JPanel panelBoardNumbers;
	static JLayeredPane layeredGameBoard;

	static  int WIDTH = 650;
	static  int DEFAULT_HEIGHT = 620;

	static boolean firstGame = true;

	static JButton col1_button = new JButton("1");
	static JButton col2_button = new JButton("2");
	static JButton col3_button = new JButton("3");
	static JButton col4_button = new JButton("4");
	static JButton col5_button = new JButton("5");
	static JButton col6_button = new JButton("6");
	static JButton col7_button = new JButton("7");
	static JButton col8_button = new JButton("8");
	static JButton[] buttons = new JButton[] 
			{col1_button, col2_button, col3_button, col4_button,
					col5_button, col6_button, col7_button,col8_button};

	static JLabel turnMessage;

	static Ai ai;

	

	public static JLabel checkerLabel = null;

	
	private static int humanPlayerUndoRow;
	private static int humanPlayerUndoCol;
	private static int humanPlayerUndoLetter;
	private static JLabel humanPlayerUndoCheckerLabel;

	// Menu bars and items
	static JMenuBar barMenu;
	static JMenu fileMenu;
	static JMenuItem newGameItem;
	//	JMenuItem undoItem;
	//	JMenuItem settingsItem;
	static JMenuItem exitItem;
	static JMenu helpMenu;
	static JMenuItem howToPlayItem;
	static JMenuItem aboutItem;

	public Graphics() {

	}


	// add the option bar
	private static void menu() {

		// menu bar.
		barMenu = new JMenuBar();

		fileMenu = new JMenu("File");
		newGameItem = new JMenuItem("New Game");
		//		settingsItem = new JMenuItem("Settings");
		exitItem = new JMenuItem("Exit");



		fileMenu.add(newGameItem);
		fileMenu.add(exitItem);

		helpMenu = new JMenu("Help");
		howToPlayItem = new JMenuItem("How to Play");
		helpMenu.add(howToPlayItem);


		newGameItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					createNewGame();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				setAllButtonsEnabled(true);
			}
		});


		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		howToPlayItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"Click on the buttons 1-8 to insert a new checker."
								+ "\nTo win you must place 5 checkers in an row, horizontally, vertically or diagonally.",
								"How to Play", JOptionPane.INFORMATION_MESSAGE);
			}
		});


		barMenu.add(fileMenu);
		barMenu.add(helpMenu);

		frameMainWindow.setJMenuBar(barMenu);
		// set the menu visible
		frameMainWindow.setVisible(true);

	}


	// main Connect-5 board.
	public static JLayeredPane createLayeredBoard() {
		layeredGameBoard = new JLayeredPane();
		layeredGameBoard.setPreferredSize(new Dimension(WIDTH, DEFAULT_HEIGHT));
		layeredGameBoard.setBorder(BorderFactory.createTitledBorder("Connect-5"));

		ImageIcon imageBoard = new ImageIcon("res/images/Board.png");
		JLabel imageBoardLabel = new JLabel(imageBoard);

		imageBoardLabel.setBounds(20, 20, imageBoard.getIconWidth(), imageBoard.getIconHeight());
		layeredGameBoard.add(imageBoardLabel, 0, 1);

		return layeredGameBoard;
	}


	public static void createNewGame() throws IOException {

		
		board = new Board();

		if (frameMainWindow != null) frameMainWindow.dispose();
		
		frameMainWindow = new JFrame("Lab 7 Connect-5");
		// put the window in the center
		windows_center(frameMainWindow, WIDTH, DEFAULT_HEIGHT);
		Component compMainWindowContents = createContentComponents();
		frameMainWindow.getContentPane().add(compMainWindowContents, BorderLayout.CENTER);

		frameMainWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		
		frameMainWindow.setFocusable(true);

		// 
		frameMainWindow.pack();
		// set board visible
		 frameMainWindow.setVisible(true);

		// Add turn label on the botton
		JToolBar tools = new JToolBar();
		tools.setFloatable(false);
		frameMainWindow.add(tools, BorderLayout.PAGE_END);
		turnMessage = new JLabel("Turn: " + board.getTurn());
		tools.add(turnMessage);

		menu();

		if (Parameters.gameMode == Constants.HumanVsAi) {
			ai = new Ai(Parameters.maxDepth1, Constants.O);
			if (board.getLastSymbolPlayed() == Constants.X)
				aiMove(ai);
		}
	}


	// It centers the window on screen.
	public static void windows_center(Window frame, int width, int height) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) (dimension.getWidth() - frame.getWidth() - width) / 2;
		int y = (int) (dimension.getHeight() - frame.getHeight() - height) / 2;
		frame.setLocation(x, y);
	}


	// It finds which player plays next and makes a move on the board.
	public static void playAMove(int col) {
		board.setOverflowOccured(false);

		int previousRow = board.getLastMove().getRow();
		int previousCol = board.getLastMove().getCol();
		int previousLetter = board.getLastSymbolPlayed();

		if (board.getLastSymbolPlayed() == Constants.O) {
			board.makeMove(col, Constants.X);
		} else {
			board.makeMove(col, Constants.O);
		}

		if (board.hasOverflowOccured()) {
			board.getLastMove().setRow(previousRow);
			board.getLastMove().setCol(previousCol);
			board.setLastSymbolPlayed(previousLetter);
		}

	}


	// It places a checker on the board.
	public static void placeAChecker(int color, int row, int col) {
		String colorString = Parameters.ColorNameAndNumber(color);
		int xOffset = 75 * col;
		int yOffset = 75 * row;
		//ImageIcon checkerIcon = new ImageIcon(ResourceLoader.load("images/" + colorString + ".png"));
		ImageIcon checkerIcon = null;
		if (colorString == "Red")
        {
            checkerIcon = new ImageIcon("res/images/RED.png");
        }
        else if (colorString == "Yellow")
        {
            checkerIcon = new ImageIcon("res/images/YELLOW.png");
        }
		checkerLabel = new JLabel(checkerIcon);
		checkerLabel.setBounds(27 + xOffset, 27 + yOffset, checkerIcon.getIconWidth(),checkerIcon.getIconHeight());
		layeredGameBoard.add(checkerLabel, 0, 0);
		frameMainWindow.paint(frameMainWindow.getGraphics());
	}


	
	


	// the dynamic of the game is here, control turn, disk, calls sound effects
	public static void game() throws IOException {

		turnMessage.setText("Turn: " + board.getTurn());

		int row = board.getLastMove().getRow();
		int col = board.getLastMove().getCol();
		int currentPlayer = board.getLastSymbolPlayed();
		
		
		if (currentPlayer == Constants.X) {
			// places the checker in the correct place.
			placeAChecker(Parameters.player1Color, row, col);
			Board.playMusic("res/Sound/Player_yellow.wav");
		}

		if (currentPlayer == Constants.O) {
			// places the checker in the place.
			try {
				Thread.sleep(1200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			placeAChecker(Parameters.player2Color, row, col);
			Board.playMusic("res/Sound/Player_red.wav");
		}

		System.out.println("Turn: " + board.getTurn());
		Board.printBoard(board.getGameBoard());
		System.out.println("\n_____________________________");
		//Board.memory(board.getGameBoard());
		//Board.saveGameDataToFile("\\Users\\leona\\Desktop\\Memory.txt");
		if (board.checkGameOver()) {
			gameOver();
		}


	}

	// Call AI to make a move
	public static void aiMove(Ai ai) throws IOException{
		Move aiMove = ai.miniMax(board);
		board.makeMove(aiMove.getCol(), ai.getAiLetter());
		game();
	}


	public static void setAllButtonsEnabled(boolean b) {
		for (JButton button: buttons) {
			button.setEnabled(b);
		}
	}


	
	public static Component createContentComponents() {

		// Create a panel to set up the board buttons.
		panelBoardNumbers = new JPanel();
		panelBoardNumbers.setLayout(new GridLayout(1, 2, 4, 4));
		panelBoardNumbers.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

		if (firstGame) {

			for (int i=0; i<buttons.length; i++) {
				JButton button = buttons[i];
				int column = i;

				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						playAMove(column);
						if (!board.hasOverflowOccured()) {
							try {
								game();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						
							if (Parameters.gameMode == Constants.HumanVsAi)
								try {
									aiMove(ai);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
						}
						frameMainWindow.requestFocusInWindow();
					}
				});
			}				

			firstGame = false;
		}

		for (JButton button: buttons) {
			panelBoardNumbers.add(button);
		}

		// main Connect5 board creation
		layeredGameBoard = createLayeredBoard();

		// panel creation to store all the elements of the board
		panelMain = new JPanel();
		panelMain.setLayout(new BorderLayout());
		panelMain.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//panelMain.setBackground(new Color(0, 0, 0));
		// add button and main board components to panelMain
		panelMain.add(panelBoardNumbers, BorderLayout.NORTH);
		panelMain.add(layeredGameBoard, BorderLayout.CENTER);

		frameMainWindow.setResizable(false);
		return panelMain;
	}


	//called only of the game is over.
	// We can check if the game is over by calling the method "checkGameOver()"
	public static void gameOver() throws IOException {
		board.setGameOver(true);

		int choice = 0;
		if (board.getWinner() == Constants.X) {
			if (Parameters.gameMode == Constants.HumanVsAi)
				choice = JOptionPane.showConfirmDialog(null,
						"You win! Would like to play it again?",
						"GAME OVER", JOptionPane.YES_NO_OPTION);
		} else if (board.getWinner() == Constants.O) 
			if (Parameters.gameMode == Constants.HumanVsAi)
				choice = JOptionPane.showConfirmDialog(null,
						"Computer AI wins! Would like to play it again?",
						"GAME OVER", JOptionPane.YES_NO_OPTION);
						
		if (choice == JOptionPane.YES_OPTION) {
			createNewGame();
		} else {
			// Disable buttons
			setAllButtonsEnabled(false);
			System.exit(0);
		}

	}
	//  start menu and after the game
	public static void startMenu() throws IOException {
		//create frames 
		JFrame mainMenu = new JFrame("Main Menu");
		JButton play = new JButton("Start Game");
		ImageIcon logo = new ImageIcon("res/images/images.png");
	    JLabel logoLabel = new JLabel(logo);
		
		
	    
	    Board.playMusic("res/Sound/intro2.wav");
	    
	    //add everything to the frame
	    logoLabel.setBounds(40,-170,500,500);
		mainMenu.setSize(600,350);
		mainMenu.setVisible(true);
		mainMenu.getContentPane().setBackground(Color.WHITE);
		mainMenu.setLocationRelativeTo(null);
		mainMenu.getContentPane().setLayout(null);
		mainMenu.add(logoLabel);
		mainMenu.add(play);
		mainMenu.setResizable(true);
		play.setBounds(250,200,100,50);
		play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if(e.getSource()==play){
            	  Graphics connect5 = new Graphics();
            	  
          		try {
					Graphics.createNewGame();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
          	mainMenu.dispose();
            }}
        });
		
	}
	

	public static void main(String[] args) throws IOException{
		Graphics.startMenu();
	}


}
