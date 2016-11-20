package Drivers;

import goController.BoardParser;
import goController.BoardParserString;
import goController.GoBoard;
import goController.Stone;
import goController.StoneOwner;

public class BoardTestsDriver {

	private static String simpleBoardString = 
			   "-x----\n"
	  		 + "x-----\n"
	  		 + "-x----\n"
	  		 + "------\n"
	  		 + "------\n";
	
	
	private static GoBoard board;
	private static Stone[][] stonePositions;
	
	public static void main(String[] args) {
		
		
		BoardParser parser = new BoardParserString(simpleBoardString);
		stonePositions = parser.parse();
		
		//height(5) width(6)
		board = new GoBoard(5, 6);
		
		board.setStonePositions(stonePositions);
		
		System.out.println(stonePositions.length);
		System.out.println(stonePositions[0].length);
		
		System.out.println(stonePositions[2][1]);
		System.out.println(stonePositions[1][2]);
		System.out.println(stonePositions[4][5]);
		System.out.println(board.isInBounds(4, 5));
//		System.out.println(stonePositions[5][4]);
		
		for(int h=0; h<stonePositions.length; h++)
		{
			for(int w=0; w<stonePositions[0].length; w++)
			{
				System.out.print("(" + h +","+ w + ")" + (stonePositions[h][w].getOwner() == StoneOwner.EMPTY? "-":"x"));
			}
			System.out.println();
		}
	}
}
