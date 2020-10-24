package connect5;


public class Parameters {
	
	/* Default values */
	public static int gameMode = Constants.HumanVsAi;
	public static int maxDepth1 = 5;
	public static int maxDepth2 = 5;
	public static int player1Color = Constants.RED;
	public static int player2Color = Constants.YELLOW;
	
	
	public static final String ColorNameAndNumber(int number) {
		switch (number) {
			
			case 1:
				return "Yellow";
			default:
				return "Red";
		}
	}
	
	
}
