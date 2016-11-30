import goController.Stone;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class StoneGUI extends Circle{

	private Stone stone;
	
	public StoneGUI(Stone stone, double centerX, double centerY, double radius, Paint paint) {
		super(centerX, centerY, radius, paint);
		this.stone = stone;
	}
	
	
	public Stone getStone() {
		return stone;
	}
	
	
}
