package goController;

public class Stone {

	private StoneOwner owner;
	private int row;
	private int col;
	
	
	public Stone(StoneOwner owner, int row, int col) {
		this.owner = owner;
		this.row = row;
		this.col = col;
	}
	
	public StoneOwner getOwner() {
		return owner;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	@Override
	public String toString() {
		return "Stone owned by: " + owner.name();
	}
}
