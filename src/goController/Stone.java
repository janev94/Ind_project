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
	
	public Stone(Stone stone) {
		this.owner = stone.owner;
		this.row = stone.row;
		this.col = stone.col;
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
	
	public void setOwner(StoneOwner owner) {
		this.owner = owner;
	}
	
	@Override
	public String toString() {
		return "Stone(" + row + ", " + col + ") owned by: " + owner.name();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stone other = (Stone) obj;
		if (col != other.col)
			return false;
		if (owner != other.owner)
			return false;
		if (row != other.row)
			return false;
		return true;
	}
	
	
}
