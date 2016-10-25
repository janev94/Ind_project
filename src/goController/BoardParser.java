package goController;

public abstract class BoardParser {

	
//	public static BoardParser createParser(String type) {
//		
//		return new BoardParserString();
//	}
	
	public abstract Stone[][] parse();
	
	public Stone[][] setUpEmptyBoard(int width, int height){
		Stone[][] stonePositions = new Stone[width][height];
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++) {
				stonePositions[x][y] = new Stone(StoneOwner.EMPTY, x, y);
			}
		}
		
		return stonePositions;
	}
}
