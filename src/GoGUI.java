import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GoGUI extends Application {

	int boardWidth = 9;
	int boardHeight = 5;
	
	private int SQ_SIDE = 40;
	private int RADIUS = 10;

	private boolean turn = true;

	
	@Override
	public void start(Stage stage) {
		
		Group root = drawScene();
        Scene scene = new Scene(root, SQ_SIDE * (boardWidth + 1), SQ_SIDE * (boardHeight + 1));

        stage.setTitle("Go appl");
        stage.setScene(scene);
        stage.show();
    }

	public Group drawScene() {
		List<Circle> positions = new ArrayList<>();
		List<Line> lines = new ArrayList<>();
		List<Rectangle> rects = new ArrayList<>();
		
		
		
		for(int y=0;y < boardHeight - 1; y++) {
			for( int x=0;x < boardWidth - 1; x++) {
				Rectangle rect = new Rectangle(x * SQ_SIDE + (SQ_SIDE + RADIUS), y * SQ_SIDE + SQ_SIDE, SQ_SIDE, SQ_SIDE);
				rect.setFill(Color.BURLYWOOD);
				rect.setStrokeWidth(1);
				rect.setStroke(Color.BLACK);
				rect.toBack();
				rects.add(rect);
			}
		}
	
		
		for(int y=0; y < boardHeight; y++) {
			for(int x=0; x < boardWidth; x++) {
				Circle circ = new Circle((x+1) * SQ_SIDE + RADIUS, (y+1) * SQ_SIDE,
						RADIUS, Color.TRANSPARENT);
				circ.setStrokeWidth(1);
				circ.setStroke(Color.BLACK);
				
				circ.setOnMouseClicked(e -> { 
					Circle c = (Circle) e.getSource();
					c.setFill(turn?Color.BLACK: Color.rgb(255, 0, 0, 1.0));
					c.toFront();
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
//        root.getChildren().addAll(positions);
        //root.getChildren().addAll(lines);
        root.getChildren().addAll(rects);
        root.getChildren().addAll(positions);
		return root;
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	
}
