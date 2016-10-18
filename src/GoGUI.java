import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class GoGUI extends Application {

	int boardWidth = 10;
	int boardHeight = 5;

	private boolean turn = true;

	
	@Override
	public void start(Stage stage) {
		
		List<Circle> positions = new ArrayList<>();
		List<Line> lines = new ArrayList<>();
		
		
		//consider doing it with squares (enables filling etc)
		for(int y=0; y < boardHeight; y++) {
			Line xLine = new Line(50, (y+1) * 40
					, boardWidth * 40 + 10, (y+1) * 40);
			lines.add(xLine);
		}
		
		for(int y=0; y < boardWidth; y++) {
			
			Line yLine = new Line((y+1) * 40 + 10, 40,  
					(y+1) * 40 + 10, boardHeight * 40);
			lines.add(yLine);
		
		}
	
		
		for(int y=0; y < boardHeight; y++) {
			for(int x=0; x < boardWidth; x++) {
				Circle circ = new Circle((x+1) * 40 + 10, (y+1) * 40,
						10, Color.TRANSPARENT);
				circ.setStrokeWidth(1);
				circ.setStroke(Color.BLACK);
				
				circ.setOnMouseClicked(e -> { 
					Circle c = (Circle) e.getSource();
					c.setFill(turn?Color.BLACK: Color.WHITESMOKE);
					turn = !turn;
				});
				
				positions.add(circ);
			}
		}
		
        Circle circ = new Circle(40, 40, 30);

        circ.setFill(Color.TRANSPARENT);
        circ.setStrokeWidth(3);
        circ.setStroke(Color.BLACK);
        Group root = new Group();
        root.getChildren().addAll(positions);
        root.getChildren().addAll(lines);
        
        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("Go appl");
        stage.setScene(scene);
        stage.show();
    }

	public static void main(String[] args) {
		launch(args);
	}
	
	
}
