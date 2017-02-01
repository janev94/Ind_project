package goController;

public class GoGoal {

	private GoGoalEnum goal;
	
	private int width;
	private int height;
	
	public GoGoal(GoGoalEnum goal, int width, int height)
	{
		this.goal = goal;
		this.width = width;
		this.height = height;
	}
	
	public GoGoalEnum getGoal() {
		return goal;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
