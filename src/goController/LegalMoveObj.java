package goController;

public class LegalMoveObj {

	private boolean isLegal;
	private Stone[][] stonePositions;

	public LegalMoveObj() {
		isLegal = false;
		stonePositions = null;
	}
	
	public Stone[][] getStonePositions() {
		return stonePositions;
	}
	
	public boolean isLegal() {
		return isLegal;
	}
	
	public void setLegal(boolean isLegal) {
		this.isLegal = isLegal;
	}
	
	public void setStonePositions(Stone[][] currentState) {
		Stone[][] currentStateCopy = new Stone[currentState.length][currentState[0].length];
		for(int i = 0; i < currentState.length; i++){
			for(int j = 0; j < currentState[i].length; j++)
			{
				currentStateCopy[i][j] = new Stone(currentState[i][j]);
			}
		}
		
		this.stonePositions = currentStateCopy;
	}
	
	/**
	 * Does not do deep copy
	 * @param board
	 */
	@Deprecated
	public LegalMoveObj(GoBoard board) {
		
		stonePositions = board.getStonePositions();
	}
}
