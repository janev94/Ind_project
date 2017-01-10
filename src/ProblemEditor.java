import goController.GoBoard;
import goController.Stone;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ProblemEditor extends Application{

	@Override
	public void start(Stage stage) throws Exception {

		Group root = new Group();
		
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(5);
		grid.setHgap(5);
		
		Label width = new Label("width:");
		GridPane.setConstraints(width, 0, 0);
		TextField widthInput = new TextField();
		GridPane.setConstraints(widthInput, 1, 0);
		
		Label height = new Label("height:");
		GridPane.setConstraints(height, 0, 1);
		TextField heightInput = new TextField();
		GridPane.setConstraints(heightInput, 1, 1);
		
		Button sketchIt = new Button("Sketch it");
		GridPane.setConstraints(sketchIt, 2, 0);
		sketchIt.setOnMouseClicked(e -> {
			int w = Integer.valueOf(width.getText());
			int h = Integer.valueOf(height.getText());
			
			GoBoard board = new GoBoard(w, h);
			Stone[][] positions = board.getStonePositions();
			
			
		});

		grid.getChildren().addAll(width, widthInput, height, heightInput, sketchIt);
		
		GoGUI gg = new GoGUI();
		Group drawer = gg.drawScene();
		GridPane.setConstraints(drawer, 0, 3);
		grid.getChildren().add(drawer);
		
		root.getChildren().addAll(grid);
		
		Scene scene = new Scene(root, 800, 600);

		stage.setTitle("Editor");
		stage.setScene(scene);
		stage.show();
	}

	
	public static void main(String[] args) {
		launch(args);
	}
}
