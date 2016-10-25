package goController;

public class Stone {

	private StoneOwner owner;
	
	public Stone(StoneOwner owner) {
		this.owner = owner;
	}
	
	public StoneOwner getOwner() {
		return owner;
	}
	
	@Override
	public String toString() {
		return "Stone owned by: " + owner.name();
	}
}
