package goController;

public enum StoneOwner {
	BLACK, WHITE, EMPTY, NUSED;
	
	public StoneOwner getOpposingColour() {
		if (this == StoneOwner.BLACK)
			return StoneOwner.WHITE;
		else if (this == StoneOwner.WHITE)
			return BLACK;
		else 
			return EMPTY;
	}
	
//	@Override
//	public String toString() {
//		return name();
//	}
	
}
