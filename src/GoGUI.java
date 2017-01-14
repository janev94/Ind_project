import java.util.ArrayList;
import java.util.List;

import goController.BoardParser;
import goController.BoardParserString;
import goController.GoBoard;
import goController.Stone;
import goController.StoneOwner;
import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GoGUI extends Application {

	Stone[][] stonePositions;
	GoBoard board;
	
	int boardWidth = 9;
	int boardHeight = 5;
	
	private int SQ_SIDE = 40;
	private int RADIUS = 10;

	private boolean turn = false;
	private StoneOwner playerToMove;

	private List<Circle> positions;
	private Label label;
	
	@Override
	public void start(Stage stage) {
		
		Group root = drawScene();
        Scene scene = new Scene(root, SQ_SIDE * (boardWidth + 1), SQ_SIDE * (boardHeight + 1));

        stage.setTitle("Go appl");
        stage.setScene(scene);
        stage.show();
    }

	public Group drawScene() {
		positions = new ArrayList<>();
		List<Line> lines = new ArrayList<>();
		List<Rectangle> rects = new ArrayList<>();
		
		setUpBoard();
		
		label = new Label("Player to move: " + playerToMove.toString());
		
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
				Paint paint = Color.TRANSPARENT;
				if(stonePositions[y][x].getOwner() == StoneOwner.BLACK)
					paint = Color.BLACK;
				else if(stonePositions[y][x].getOwner() == StoneOwner.WHITE)
					paint = Color.WHITESMOKE;
					
//				Circle circ = new Circle((x+1) * SQ_SIDE + RADIUS, (y+1) * SQ_SIDE,
//						RADIUS, paint);
				StoneGUI stone = new StoneGUI(stonePositions[y][x], (x+1) * SQ_SIDE + RADIUS,
						(y+1) * SQ_SIDE, RADIUS, paint);
				
				stone.setStrokeWidth(1);
				stone.setStroke(Color.BLACK);
				
				stone.setOnMouseClicked(e -> { 
					StoneGUI st = (StoneGUI) e.getSource();
					System.out.println("Row:" + st.getStone().getRow() + " Col: " + st.getStone().getCol());
					
					//Play move returns true if current move is possible
					if(board.playMove(st.getStone().getRow(), st.getStone().getCol(), playerToMove))
					{
						//st.setFill(turn?Color.BLACK: Color.WHITESMOKE);
						renderBoard();
						playerToMove = playerToMove.getOpposingColour();
						board.setPlayerToMove(playerToMove);
						label.setText("Player to move: " + playerToMove);
						turn = !turn;
					} else
					{
						System.out.println("Illegal move");
						System.out.println(playerToMove);
					}
					System.out.println(st.getStone().getRow() + " " + st.getStone().getCol());
//					st.setFill(turn?Color.BLACK: Color.WHITESMOKE);
//					st.toFront();
//					turn = !turn;
				});
				
				positions.add(stone);
			}
		}
		
		//check for legal moves
		//checkLegals();
		
        Circle circ = new Circle(40, 40, 30);

        circ.setFill(Color.TRANSPARENT);
        circ.setStrokeWidth(3);
        circ.setStroke(Color.BLACK);
        Group root = new Group();
//        root.getChildren().addAll(positions);
        //root.getChildren().addAll(lines);
        root.getChildren().addAll(rects);
        root.getChildren().addAll(positions);
        root.getChildren().add(label);
		return root;
	}

	
	
	private void renderBoard() {
		for(int y=0; y < boardHeight; y++) {
			for(int x=0; x < boardWidth; x++) {
				Paint paint = Color.TRANSPARENT;
				if(stonePositions[y][x].getOwner() == StoneOwner.BLACK)
					paint = Color.BLACK;
				else if(stonePositions[y][x].getOwner() == StoneOwner.WHITE)
					paint = Color.WHITESMOKE;
			
				positions.get(y*boardWidth + x).setFill(paint);
			}
		}
		
	}
	
	
	private void checkLegals() {
		for(int y=0; y < boardHeight; y++) {
			for(int x=0; x < boardWidth; x++) {
//				Paint paint = Color.TRANSPARENT;
//				if(stonePositions[y][x].getOwner() == StoneOwner.BLACK)
//					paint = Color.BLACK;
//				else if(stonePositions[y][x].getOwner() == StoneOwner.WHITE)
//					paint = Color.WHITESMOKE;
			
				StoneGUI st = (StoneGUI) positions.get(y*boardWidth + x);
				
				//if owner is not empty move is illegal by default, skip colouring
				if(st.getStone().getOwner() != StoneOwner.EMPTY)
					continue;
				
				Paint paint;
				if(board.isLegalMove(st.getStone().getRow(), st.getStone().getCol(), playerToMove).isLegal()) {
					paint = Color.LIMEGREEN;
				}
				else 
					paint = Color.RED;
				
//				StoneGUI stone = new StoneGUI(stonePositions[y][x], (x+1) * SQ_SIDE + RADIUS,
//						(y+1) * SQ_SIDE, RADIUS, Color.TRANSPARENT);
				
				st.setFill(paint);
				
//				stone.setStrokeWidth(5);
//				stone.setStroke(paint);
			}
		}
	}
	

	private void setUpBoard() {
		String selfCaptureBoardString = 
				    "---xxxx--\n"
				  + "--x-oxxx-\n"
				  + "--xooxxx-\n"
				  + "---xxxx--\n"
				  + "---------\n";
		
		String koRuleBoardString = 
				   "-------\n"
				 + "--xo---\n"
				 + "-x-xo--\n"
				 + "--xo---\n"
				 + "-------\n";
		
		String parsedString = selfCaptureBoardString;
		
		BoardParser parser = new BoardParserString(parsedString);
		
		String[] rows = parsedString.split("\n");
		System.out.println(rows.length);
		System.out.println(rows[0].length());
		
		boardWidth = rows[0].length();
		boardHeight = rows.length;
		
		stonePositions = parser.parse();
		board = new GoBoard(boardHeight, boardWidth);
		
		board.setStonePositions(stonePositions);
		board.getPreviousStates().addState(stonePositions);
		playerToMove = StoneOwner.WHITE;
		board.setPlayerToMove(playerToMove);
	}

	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
}
