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

	int boardWidth = 5;
	int boardHeight = 5;
	
	@Override
	public void start(Stage stage) {
		
		List<Circle> positions = new ArrayList<>();
		List<Line> lines = new ArrayList<>();
		for(int y=0; y < boardHeight; y++) {
			for(int x=0; x < boardWidth; x++) {

				if(x < boardWidth - 1) {
					Line xLine = new Line((x + 1 ) * 40 + 20, (y+1) * 40
										, (x+2) * 40, (y+1) * 40);
					lines.add(xLine);
				}

				if( y < boardHeight - 1) {
					Line yLine = new Line((x + 1) * 40 + 10, (y+1) * 40 + 10 
										,(x + 1) * 40 + 10, (y+2) * 40 - 10);
					lines.add(yLine);
				}
				
				Circle circ = new Circle((x+1) * 40 + 10, (y+1) * 40,
						10, Color.TRANSPARENT);
				circ.setStrokeWidth(1);
				circ.setStroke(Color.BLACK);
				
				circ.setOnMouseClicked(e -> { 
					Circle c = (Circle) e.getSource();
					c.setFill(Color.BLACK);
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

        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.show();
    }

	public static void main(String[] args) {
		launch(args);
	}
	
	
}
